package uk.co.softrank.eventplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import uk.co.softrank.eventplanner.data.local.entity.TaskEntity

@Dao
interface TaskDao {
    @Query(
        """
        SELECT * FROM tasks
        WHERE (:eventId IS NULL AND eventId IS NULL) OR eventId = :eventId
        ORDER BY isCompleted ASC,
                 CASE priority WHEN 'High' THEN 0 WHEN 'Medium' THEN 1 ELSE 2 END,
                 dueDateEpochDay IS NULL, dueDateEpochDay ASC,
                 title COLLATE NOCASE ASC
        """
    )
    fun observeForEvent(eventId: Long?): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, updatedAt DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun observeOpenCount(): Flow<Int>
}
