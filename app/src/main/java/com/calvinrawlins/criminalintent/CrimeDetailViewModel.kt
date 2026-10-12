package com.calvinrawlins.criminalintent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class CrimeDetailViewModel(
    application: Application,
    crimeId: UUID
) : AndroidViewModel(application) {

    private val repository =
        CrimeRepository.get(application)

    val crime =
        repository
            .getCrime(crimeId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    fun updateCrime(crime: Crime) {
        viewModelScope.launch {
            repository.updateCrime(crime)
        }
    }

    fun deleteCrime(
        crime: Crime,
        onDeleted: () -> Unit
    ) {
        viewModelScope.launch {
            repository.deleteCrime(crime)
            onDeleted()
        }
    }
}

class CrimeDetailViewModelFactory(
    private val application: Application,
    private val crimeId: UUID
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        return CrimeDetailViewModel(
            application,
            crimeId
        ) as T
    }
}