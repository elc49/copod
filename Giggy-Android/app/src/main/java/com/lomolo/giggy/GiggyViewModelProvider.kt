package com.lomolo.giggy

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lomolo.giggy.viewmodels.MainViewModel

object GiggyViewModelProvider {
    val Factory = viewModelFactory {
        lateinit var mainViewModel: MainViewModel

        initializer {
            mainViewModel = MainViewModel(giggyApplication().container.giggyRestApiService)
            mainViewModel
        }
    }
}

/* Instance of Giggy App */
fun CreationExtras.giggyApplication(): GiggyApp = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GiggyApp)