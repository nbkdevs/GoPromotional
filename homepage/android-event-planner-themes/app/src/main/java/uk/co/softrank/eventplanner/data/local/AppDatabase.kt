package uk.co.softrank.eventplanner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uk.co.softrank.eventplanner.data.local.dao.EventDao
import uk.co.softrank.eventplanner.data.local.dao.ExpenseDao
import uk.co.softrank.eventplanner.data.local.dao.MerchandiseDao
import uk.co.softrank.eventplanner.data.local.dao.TaskDao
import uk.co.softrank.eventplanner.data.local.dao.ThemeFavoriteDao
import uk.co.softrank.eventplanner.data.local.entity.EventEntity
import uk.co.softrank.eventplanner.data.local.entity.ExpenseEntity
import uk.co.softrank.eventplanner.data.local.entity.MerchandiseEntity
import uk.co.softrank.eventplanner.data.local.entity.TaskEntity
import uk.co.softrank.eventplanner.data.local.entity.ThemeFavoriteEntity

@Database(
    entities = [
        EventEntity::class,
        TaskEntity::class,
        ExpenseEntity::class,
        MerchandiseEntity::class,
        ThemeFavoriteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun taskDao(): TaskDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun merchandiseDao(): MerchandiseDao
    abstract fun themeFavoriteDao(): ThemeFavoriteDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "event_planner.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
        }
    }
}
