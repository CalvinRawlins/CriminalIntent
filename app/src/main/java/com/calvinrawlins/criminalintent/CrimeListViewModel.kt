package com.calvinrawlins.criminalintent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CrimeListViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        CrimeRepository.get(application)

    val crimes =
        repository
            .getCrimes()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun addCrime(
        crime: Crime,
        onAdded: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addCrime(crime)
            onAdded()
        }
    }
}