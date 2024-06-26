package com.lomolo.giggy

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.apollographql.apollo3.cache.normalized.apolloStore
import com.lomolo.giggy.compose.screens.AccountViewModel
import com.lomolo.giggy.compose.screens.AddFarmMarketViewModel
import com.lomolo.giggy.compose.screens.CreateFarmViewModel
import com.lomolo.giggy.compose.screens.CreatePostViewModel
import com.lomolo.giggy.compose.screens.FarmMarketViewModel
import com.lomolo.giggy.compose.screens.FarmViewModel
import com.lomolo.giggy.compose.screens.SigninViewModel
import com.lomolo.giggy.viewmodels.MainViewModel
import com.lomolo.giggy.viewmodels.SessionViewModel

object GiggyViewModelProvider {
    val Factory = viewModelFactory {
        lateinit var mainViewModel: MainViewModel
        lateinit var sessionViewModel: SessionViewModel
        lateinit var storeViewModel: FarmViewModel
        lateinit var addFarmMarketViewModel: AddFarmMarketViewModel
        lateinit var farmMarketViewModel: FarmMarketViewModel
        lateinit var createFarmViewModel: CreateFarmViewModel
        lateinit var accountViewModel: AccountViewModel
        lateinit var signinViewModel: SigninViewModel
        lateinit var createPostViewModel: CreatePostViewModel

        initializer {
            mainViewModel = MainViewModel(giggyApplication().container.giggyRestApiService)
            mainViewModel
        }

        initializer {
            sessionViewModel = SessionViewModel(
                giggyApplication().container.sessionRepository,
                giggyApplication().container.apolloClient.apolloStore,
            )
            sessionViewModel
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

        initializer {
            accountViewModel = AccountViewModel(
                giggyApplication().container.giggyGraphqlApiService,
            )
            accountViewModel
        }

        initializer {
            signinViewModel = SigninViewModel(
                giggyApplication().container.sessionRepository,
                mainViewModel
            )
            signinViewModel
        }

        initializer {
            createPostViewModel = CreatePostViewModel(
                mainViewModel,
                giggyApplication().container.giggyRestApiService,
                giggyApplication().container.giggyGraphqlApiService,
            )
            createPostViewModel
        }
    }
}

/* Instance of Giggy App */
fun CreationExtras.giggyApplication(): GiggyApp = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GiggyApp)