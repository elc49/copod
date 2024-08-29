package com.lomolo.vuno

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.apollographql.apollo3.cache.normalized.apolloStore
import com.lomolo.vuno.compose.screens.AccountViewModel
import com.lomolo.vuno.compose.screens.AddFarmMarketViewModel
import com.lomolo.vuno.compose.screens.CreateFarmViewModel
import com.lomolo.vuno.compose.screens.CreatePostViewModel
import com.lomolo.vuno.compose.screens.ExploreViewModel
import com.lomolo.vuno.compose.screens.FarmStoreViewModel
import com.lomolo.vuno.compose.screens.FarmSettingsViewModel
import com.lomolo.vuno.compose.screens.FarmViewModel
import com.lomolo.vuno.compose.screens.MarketCartViewModel
import com.lomolo.vuno.compose.screens.MarketDetailsViewModel
import com.lomolo.vuno.compose.screens.MarketsViewModel
import com.lomolo.vuno.compose.screens.PaymentViewModel
import com.lomolo.vuno.compose.screens.SigninViewModel
import com.lomolo.vuno.compose.screens.UserOrdersViewModel

object VunoViewModelProvider {
    val Factory = viewModelFactory {
        lateinit var mainViewModel: MainViewModel
        lateinit var sessionViewModel: SessionViewModel
        lateinit var storeViewModel: FarmViewModel
        lateinit var addFarmMarketViewModel: AddFarmMarketViewModel
        lateinit var farmStoreViewModel: FarmStoreViewModel
        lateinit var createFarmViewModel: CreateFarmViewModel
        lateinit var accountViewModel: AccountViewModel
        lateinit var signinViewModel: SigninViewModel
        lateinit var createPostViewModel: CreatePostViewModel
        lateinit var marketsViewModel: MarketsViewModel
        lateinit var exploreViewModel: ExploreViewModel
        lateinit var paymentViewModel: PaymentViewModel
        lateinit var marketCartViewModel: MarketCartViewModel
        lateinit var userOrdersViewModel: UserOrdersViewModel
        lateinit var farmSettingsViewModel: FarmSettingsViewModel
        lateinit var marketDetailsViewModel: MarketDetailsViewModel

        initializer {
            mainViewModel = MainViewModel(vunoApplication().container.vunoRestApiService)
            mainViewModel
        }

        initializer {
            sessionViewModel = SessionViewModel(
                vunoApplication().container.sessionRepository,
                vunoApplication().container.apolloClient.apolloStore,
            )
            sessionViewModel
        }

        initializer {
            createPostViewModel = CreatePostViewModel(
                mainViewModel,
                vunoApplication().container.vunoRestApiService,
                vunoApplication().container.vunoGraphqlApiService,
            )
            createPostViewModel
        }

        initializer {
            storeViewModel = FarmViewModel(
                vunoApplication().container.farmRepository,
            )
            storeViewModel
        }

        initializer {
            farmStoreViewModel = FarmStoreViewModel(
                this.createSavedStateHandle(),
                vunoApplication().container.vunoGraphqlApiService,
                vunoApplication().container.apolloClient.apolloStore,
            )
            farmStoreViewModel
        }

        initializer {
            addFarmMarketViewModel = AddFarmMarketViewModel(
                vunoApplication().container.vunoRestApiService,
                farmStoreViewModel,
                vunoApplication().container.vunoGraphqlApiService,
                vunoApplication().container.apolloClient.apolloStore,
                mainViewModel,
            )
            addFarmMarketViewModel
        }

        initializer {
            createFarmViewModel = CreateFarmViewModel(
                vunoApplication().container.apolloClient.apolloStore,
                vunoApplication().container.vunoRestApiService,
                vunoApplication().container.farmRepository,
            )
            createFarmViewModel
        }

        initializer {
            accountViewModel = AccountViewModel(
                vunoApplication().container.vunoGraphqlApiService,
            )
            accountViewModel
        }

        initializer {
            signinViewModel = SigninViewModel(
                vunoApplication().container.sessionRepository,
                mainViewModel
            )
            signinViewModel
        }

        initializer {
            marketsViewModel = MarketsViewModel(
                vunoApplication().container.marketsRepository,
                mainViewModel,
                vunoApplication().container.apolloClient.apolloStore,
            )
            marketsViewModel
        }

        initializer {
            exploreViewModel = ExploreViewModel(
                vunoApplication().container.postersRepository,
                mainViewModel,
            )
            exploreViewModel
        }

        initializer {
            paymentViewModel = PaymentViewModel(
                vunoApplication().container.paymentRepository,
                sessionViewModel = sessionViewModel,
                this.createSavedStateHandle(),
            )
            paymentViewModel
        }

        initializer {
            marketCartViewModel = MarketCartViewModel(
                vunoApplication().container.marketsRepository,
                vunoApplication().container.apolloClient.apolloStore,
                marketsViewModel,
            )
            marketCartViewModel
        }

        initializer {
            userOrdersViewModel = UserOrdersViewModel(
                vunoApplication().container.marketsRepository,
            )
            userOrdersViewModel
        }

        initializer {
            farmSettingsViewModel = FarmSettingsViewModel(
                vunoApplication().container.vunoGraphqlApiService,
                vunoApplication().container.vunoRestApiService,
                this.createSavedStateHandle(),
                vunoApplication().container.apolloClient.apolloStore,
            )
            farmSettingsViewModel
        }

        initializer {
            marketDetailsViewModel = MarketDetailsViewModel(
                this.createSavedStateHandle(),
                vunoApplication().container.marketsRepository,
            )

            marketDetailsViewModel
        }
    }
}

/* Instance of Vuno App */
fun CreationExtras.vunoApplication(): VunoApp = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VunoApp)