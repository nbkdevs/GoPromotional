package uk.co.softrank.bagpack.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "lists")
data class PackListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = PackListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listId")]
)
data class PackItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val title: String,
    val packed: Boolean,
    val createdAt: Long
)

@Dao
interface PackDao {
    @Query("SELECT * FROM lists ORDER BY updatedAt DESC")
    fun observeLists(): Flow<List<PackListEntity>>

    @Query("SELECT * FROM lists WHERE id = :id")
    fun observeList(id: Long): Flow<PackListEntity?>

    @Query("SELECT * FROM items WHERE listId = :listId ORDER BY packed ASC, title COLLATE NOCASE")
    fun observeItems(listId: Long): Flow<List<PackItemEntity>>

    @Insert
    suspend fun insertList(list: PackListEntity): Long

    @Update
    suspend fun updateList(list: PackListEntity)

    @Query("DELETE FROM lists WHERE id = :id")
    suspend fun deleteList(id: Long)

    @Insert
    suspend fun insertItem(item: PackItemEntity): Long

    @Update
    suspend fun updateItem(item: PackItemEntity)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteItem(id: Long)

    @Query("SELECT COUNT(*) FROM lists")
    suspend fun listCount(): Int
}

@Database(entities = [PackListEntity::class, PackItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun packDao(): PackDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bagpack.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}

class PackRepository(db: AppDatabase) {
    private val dao = db.packDao()
    fun observeLists() = dao.observeLists()
    fun observeList(id: Long) = dao.observeList(id)
    fun observeItems(listId: Long) = dao.observeItems(listId)

    suspend fun createList(name: String, seed: List<String> = emptyList()): Long {
        val now = System.currentTimeMillis()
        val id = dao.insertList(PackListEntity(name = name.trim(), createdAt = now, updatedAt = now))
        seed.forEach { title ->
            dao.insertItem(PackItemEntity(listId = id, title = title, packed = false, createdAt = now))
        }
        return id
    }

    suspend fun renameList(list: PackListEntity, name: String) {
        dao.updateList(list.copy(name = name.trim(), updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteList(id: Long) = dao.deleteList(id)

    suspend fun addItem(listId: Long, title: String) {
        val now = System.currentTimeMillis()
        dao.insertItem(PackItemEntity(listId = listId, title = title.trim(), packed = false, createdAt = now))
        touch(listId)
    }

    suspend fun togglePacked(item: PackItemEntity) {
        dao.updateItem(item.copy(packed = !item.packed))
        touch(item.listId)
    }

    suspend fun deleteItem(item: PackItemEntity) {
        dao.deleteItem(item.id)
        touch(item.listId)
    }

    private suspend fun touch(listId: Long) {
        // lightweight bump via rename noop if needed — skip for simplicity
    }

    companion object {
        val CONFERENCE = listOf(
            "Badge / ticket", "Notebook", "Pen", "Phone charger", "Water bottle",
            "Snacks", "Business cards", "Jacket", "Earphones", "Hand sanitiser"
        )
        val GROCERY = listOf(
            "Reusable tote", "Shopping list", "Wallet", "Produce bags", "Cool bag (if needed)"
        )
        val DAY_OUT = listOf(
            "Keys", "Wallet", "Phone", "Reusable tote", "Water bottle", "Umbrella", "Snacks"
        )
    }
}
