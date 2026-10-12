package com.calvinrawlins.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "crime")
data class Crime(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val title: String = "",
    val date: Date = Date(),
    val isSolved: Boolean = false
) {
    val photoFileName: String
        get() = "IMG_$id.jpg"
}