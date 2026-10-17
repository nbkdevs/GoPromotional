package uk.co.softrank.mugday.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "mug_logs")
data class MugLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usedReusable: Boolean,
    val drinkName: String,
    val epochDay: Long,
    val createdAt: Long,
    val note: String = ""
)

@Dao
interface MugDao {
    @Query("SELECT * FROM mug_logs ORDER BY createdAt DESC LIMIT 100")
    fun observeRecent(): Flow<List<MugLogEntity>>

    @Query("SELECT COUNT(*) FROM mug_logs WHERE usedReusable = 1 AND epochDay >= :fromDay")
    fun observeReusableSince(fromDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM mug_logs WHERE usedReusable = 0 AND epochDay >= :fromDay")
    fun observeDisposableSince(fromDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM mug_logs WHERE usedReusable = 1 AND epochDay = :day")
    fun observeReusableToday(day: Long): Flow<Int>

    @Insert
    suspend fun insert(log: MugLogEntity): Long

    @Query("DELETE FROM mug_logs WHERE id = :id")
    suspend fun delete(id: Long)
}

@Database(entities = [MugLogEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mugDao(): MugDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mugday.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}

class MugRepository(db: AppDatabase) {
    private val dao = db.mugDao()
    fun observeRecent() = dao.observeRecent()
    fun observeReusableSince(fromDay: Long) = dao.observeReusableSince(fromDay)
    fun observeDisposableSince(fromDay: Long) = dao.observeDisposableSince(fromDay)
    fun observeReusableToday(day: Long) = dao.observeReusableToday(day)

    suspend fun log(usedReusable: Boolean, drinkName: String, note: String = "") {
        val day = java.time.LocalDate.now().toEpochDay()
        dao.insert(
            MugLogEntity(
                usedReusable = usedReusable,
                drinkName = drinkName.ifBlank { if (usedReusable) "Travel mug" else "Disposable cup" },
                epochDay = day,
                createdAt = System.currentTimeMillis(),
                note = note
            )
        )
    }

    suspend fun delete(id: Long) = dao.delete(id)
}
