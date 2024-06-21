package com.lomolo.giggy

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.apollographql.apollo3.cache.normalized.apolloStore
import com.lomolo.giggy.viewmodels.AddFarmProductViewModel
import com.lomolo.giggy.viewmodels.FarmStoreProductViewModel
import com.lomolo.giggy.viewmodels.MainViewModel
import com.lomolo.giggy.viewmodels.PostingViewModel
import com.lomolo.giggy.viewmodels.SessionViewModel
import com.lomolo.giggy.viewmodels.StoreViewModel

object GiggyViewModelProvider {
    val Factory = viewModelFactory {
        lateinit var mainViewModel: MainViewModel
        lateinit var sessionViewModel: SessionViewModel
        lateinit var postingViewModel: PostingViewModel
        lateinit var storeViewModel: StoreViewModel
        lateinit var addFarmProductViewModel: AddFarmProductViewModel
        lateinit var farmStoreProductViewModel: FarmStoreProductViewModel

        initializer {
            mainViewModel = MainViewModel(giggyApplication().container.giggyRestApiService)
            mainViewModel
        }

        initializer {
            sessionViewModel = SessionViewModel(
                giggyApplication().container.sessionRepository,
                mainViewModel,
                giggyApplication().container.giggyGraphqlApiService,
                giggyApplication().container.apolloClient.apolloStore,
            )
            sessionViewModel
        }

        initializer {
            postingViewModel = PostingViewModel(
                mainViewModel,
                giggyApplication().container.giggyRestApiService,
                giggyApplication().container.giggyGraphqlApiService,
            )
            postingViewModel
        }

        initializer {
            storeViewModel = StoreViewModel(
                giggyApplication().container.giggyRestApiService,
                giggyApplication().container.storeRepository,
            )
            storeViewModel
        }

        initializer {
            farmStoreProductViewModel = FarmStoreProductViewModel(
                this.createSavedStateHandle(),
                giggyApplication().container.giggyGraphqlApiService,
            )
            farmStoreProductViewModel
        }

        initializer {
            addFarmProductViewModel = AddFarmProductViewModel(
                giggyApplication().container.giggyRestApiService,
                farmStoreProductViewModel,
                giggyApplication().container.giggyGraphqlApiService,
                giggyApplication().container.apolloClient.apolloStore,
            )
            addFarmProductViewModel
        }
    }
}

/* Instance of Giggy App */
fun CreationExtras.giggyApplication(): GiggyApp = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GiggyApp)