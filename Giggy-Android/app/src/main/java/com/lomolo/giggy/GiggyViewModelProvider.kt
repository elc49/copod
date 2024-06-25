package com.lomolo.giggy

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.apollographql.apollo3.cache.normalized.apolloStore
import com.lomolo.giggy.viewmodels.AddFarmMarketViewModel
import com.lomolo.giggy.viewmodels.CreateFarmViewModel
import com.lomolo.giggy.viewmodels.FarmMarketViewModel
import com.lomolo.giggy.viewmodels.MainViewModel
import com.lomolo.giggy.viewmodels.PostingViewModel
import com.lomolo.giggy.viewmodels.SessionViewModel
import com.lomolo.giggy.viewmodels.FarmViewModel

object GiggyViewModelProvider {
    val Factory = viewModelFactory {
        lateinit var mainViewModel: MainViewModel
        lateinit var sessionViewModel: SessionViewModel
        lateinit var postingViewModel: PostingViewModel
        lateinit var storeViewModel: FarmViewModel
        lateinit var addFarmMarketViewModel: AddFarmMarketViewModel
        lateinit var farmMarketViewModel: FarmMarketViewModel
        lateinit var createFarmViewModel: CreateFarmViewModel

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
            storeViewModel = FarmViewModel(
                giggyApplication().container.farmRepository,
            )
            storeViewModel
        }

        initializer {
            farmMarketViewModel = FarmMarketViewModel(
                this.createSavedStateHandle(),
                giggyApplication().container.giggyGraphqlApiService,
            )
            farmMarketViewModel
        }

        initializer {
            addFarmMarketViewModel = AddFarmMarketViewModel(
                giggyApplication().container.giggyRestApiService,
                farmMarketViewModel,
                giggyApplication().container.giggyGraphqlApiService,
                giggyApplication().container.apolloClient.apolloStore,
            )
            addFarmMarketViewModel
        }

        initializer {
            createFarmViewModel = CreateFarmViewModel(
                giggyApplication().container.apolloClient.apolloStore,
                giggyApplication().container.giggyRestApiService,
                giggyApplication().container.farmRepository,
            )
            createFarmViewModel
        }
    }
}

/* Instance of Giggy App */
fun CreationExtras.giggyApplication(): GiggyApp = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GiggyApp)