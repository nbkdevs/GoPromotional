package uk.co.softrank.siplog.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Entity(tableName = "sips")
data class SipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMl: Int,
    val epochDay: Long,
    val createdAt: Long,
    val note: String = ""
)

@Dao
interface SipDao {
    @Query("SELECT * FROM sips WHERE epochDay = :day ORDER BY createdAt DESC")
    fun observeDay(day: Long): Flow<List<SipEntity>>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM sips WHERE epochDay = :day")
    fun observeTotalForDay(day: Long): Flow<Int>

    @Insert
    suspend fun insert(sip: SipEntity): Long

    @Query("DELETE FROM sips WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM sips WHERE epochDay = :day")
    suspend fun clearDay(day: Long)
}

@Database(entities = [SipEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sipDao(): SipDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "siplog.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}

class SipRepository(db: AppDatabase) {
    private val dao = db.sipDao()
    fun observeDay(day: Long) = dao.observeDay(day)
    fun observeTotal(day: Long) = dao.observeTotalForDay(day)
    suspend fun add(amountMl: Int, day: Long, note: String = "") {
        dao.insert(SipEntity(amountMl = amountMl, epochDay = day, createdAt = System.currentTimeMillis(), note = note))
    }
    suspend fun delete(id: Long) = dao.delete(id)
    suspend fun clearDay(day: Long) = dao.clearDay(day)
}

private val Context.dataStore by preferencesDataStore("siplog_prefs")

class PreferencesStore(private val context: Context) {
    private val goalKey = intPreferencesKey("daily_goal_ml")
    val goalMl: Flow<Int> = context.dataStore.data.map { it[goalKey] ?: 2000 }
    suspend fun setGoal(ml: Int) {
        context.dataStore.edit { it[goalKey] = ml.coerceIn(500, 6000) }
    }
}
