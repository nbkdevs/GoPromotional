package uk.co.softrank.eventplanner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "theme_favorites")
data class ThemeFavoriteEntity(
    @PrimaryKey val themeId: String,
    val favoritedAt: Long
)
