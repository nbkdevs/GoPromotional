package uk.co.softrank.eventplanner.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("eventId")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventId: Long?,
    val title: String,
    val category: String,
    val priority: String,
    val dueDateEpochDay: Long?,
    val notes: String,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
