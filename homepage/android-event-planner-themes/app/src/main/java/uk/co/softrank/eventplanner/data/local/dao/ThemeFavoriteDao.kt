package uk.co.softrank.eventplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uk.co.softrank.eventplanner.data.local.entity.ThemeFavoriteEntity

@Dao
interface ThemeFavoriteDao {
    @Query("SELECT themeId FROM theme_favorites ORDER BY favoritedAt DESC")
    fun observeFavoriteIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: ThemeFavoriteEntity)

    @Query("DELETE FROM theme_favorites WHERE themeId = :themeId")
    suspend fun delete(themeId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM theme_favorites WHERE themeId = :themeId)")
    suspend fun isFavorite(themeId: String): Boolean
}
