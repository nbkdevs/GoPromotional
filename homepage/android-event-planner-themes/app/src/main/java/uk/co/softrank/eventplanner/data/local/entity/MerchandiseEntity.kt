package uk.co.softrank.eventplanner.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "merchandise",
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
data class MerchandiseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventId: Long,
    val name: String,
    val quantity: Int,
    val unitCost: Double,
    val status: String,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    val totalCost: Double get() = quantity * unitCost
}
