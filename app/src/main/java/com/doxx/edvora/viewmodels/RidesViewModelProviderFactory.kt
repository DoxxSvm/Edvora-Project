package com.doxx.edvora.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.doxx.edvora.repository.RidesRepository

class RidesViewModelProviderFactory(val ridesRepository: RidesRepository) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RidesViewModel(ridesRepository) as T
    }
}