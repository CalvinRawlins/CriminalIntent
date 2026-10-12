package com.calvinrawlins.criminalintent

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CrimeRepository private constructor(
    context: Context
) {

    private val crimeDao =
        CrimeDatabase
            .getInstance(context)
            .crimeDao()

    fun getCrimes(): Flow<List<Crime>> {
        return crimeDao.getCrimes()
    }

    fun getCrime(id: UUID): Flow<Crime?> {
        return crimeDao.getCrime(id)
    }

    suspend fun addCrime(crime: Crime) {
        crimeDao.addCrime(crime)
    }

    suspend fun updateCrime(crime: Crime) {
        crimeDao.updateCrime(crime)
    }

    suspend fun deleteCrime(crime: Crime) {
        crimeDao.deleteCrime(crime)
    }

    companion object {

        @Volatile
        private var INSTANCE: CrimeRepository? = null

        fun get(context: Context): CrimeRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CrimeRepository(
                    context.applicationContext
                ).also {
                    INSTANCE = it
                }
            }
        }
    }
}