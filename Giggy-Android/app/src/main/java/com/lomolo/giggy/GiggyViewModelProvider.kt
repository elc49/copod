package com.lomolo.giggy

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lomolo.giggy.viewmodels.MainViewModel
import com.lomolo.giggy.viewmodels.SessionViewModel

object GiggyViewModelProvider {
    val Factory = viewModelFactory {
        lateinit var mainViewModel: MainViewModel
        lateinit var sessionViewModel: SessionViewModel

        initializer {
            mainViewModel = MainViewModel(giggyApplication().container.giggyRestApiService)
            mainViewModel
        }

        initializer {
            sessionViewModel = SessionViewModel(giggyApplication().container.sessionRepository, mainViewModel)
            sessionViewModel
        }
    }
}

/* Instance of Giggy App */
fun CreationExtras.giggyApplication(): GiggyApp = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GiggyApp)