package uk.co.softrank.eventplanner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val dateEpochDay: Long?,
    val timeMinutes: Int?,
    val venue: String,
    val expectedAttendees: Int?,
    val notes: String,
    val budgetTotal: Double,
    val createdAt: Long,
    val updatedAt: Long
)
