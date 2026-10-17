package uk.co.softrank.eventplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import uk.co.softrank.eventplanner.data.local.entity.MerchandiseEntity

@Dao
interface MerchandiseDao {
    @Query("SELECT * FROM merchandise WHERE eventId = :eventId ORDER BY name COLLATE NOCASE")
    fun observeForEvent(eventId: Long): Flow<List<MerchandiseEntity>>

    @Query("SELECT * FROM merchandise WHERE id = :id")
    suspend fun getById(id: Long): MerchandiseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MerchandiseEntity): Long

    @Update
    suspend fun update(item: MerchandiseEntity)

    @Delete
    suspend fun delete(item: MerchandiseEntity)
}
