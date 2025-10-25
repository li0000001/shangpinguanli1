package com.expirytracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val productionDate: Long? = null,
    val shelfLifeDays: Int? = null,
    val expiryDate: Long,
    val reminderDaysBefore: Int = 3,
    val calendarEventId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
