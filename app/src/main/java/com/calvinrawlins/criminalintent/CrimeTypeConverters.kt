package com.calvinrawlins.criminalintent

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class CrimeTypeConverters {

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(milliseconds: Long): Date {
        return Date(milliseconds)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String): UUID {
        return UUID.fromString(uuid)
    }
}