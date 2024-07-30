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
import com.lomolo.vuno.compose.screens.DashboardViewModel
import com.lomolo.vuno.compose.screens.FarmMarketViewModel
import com.lomolo.vuno.compose.screens.FarmViewModel
import com.lomolo.vuno.compose.screens.MarketCartViewModel
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
        lateinit var farmMarketViewModel: FarmMarketViewModel
        lateinit var createFarmViewModel: CreateFarmViewModel
        lateinit var accountViewModel: AccountViewModel
        lateinit var signinViewModel: SigninViewModel
        lateinit var createPostViewModel: CreatePostViewModel
        lateinit var marketsViewModel: MarketsViewModel
        lateinit var dashboardViewModel: DashboardViewModel
        lateinit var paymentViewModel: PaymentViewModel
        lateinit var marketCartViewModel: MarketCartViewModel
        lateinit var userOrdersViewModel: UserOrdersViewModel

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
            createPostViewModel = CreatePostViewModel(
                mainViewModel,
                giggyApplication().container.giggyRestApiService,
                giggyApplication().container.giggyGraphqlApiService,
            )
            createPostViewModel
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
                giggyApplication().container.apolloClient.apolloStore,
            )
            farmMarketViewModel
        }

        initializer {
            addFarmMarketViewModel = AddFarmMarketViewModel(
                giggyApplication().container.giggyRestApiService,
                farmMarketViewModel,
                giggyApplication().container.giggyGraphqlApiService,
                giggyApplication().container.apolloClient.apolloStore,
                mainViewModel,
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
            marketsViewModel = MarketsViewModel(
                giggyApplication().container.marketsRepository,
                mainViewModel,
                giggyApplication().container.apolloClient.apolloStore,
            )
            marketsViewModel
        }

        initializer {
            dashboardViewModel = DashboardViewModel(
                giggyApplication().container.postersRepository,
                mainViewModel,
            )
            dashboardViewModel
        }

        initializer {
            paymentViewModel = PaymentViewModel(
                giggyApplication().container.paymentRepository,
                sessionViewModel = sessionViewModel,
                this.createSavedStateHandle(),
            )
            paymentViewModel
        }

        initializer {
            marketCartViewModel = MarketCartViewModel(
                giggyApplication().container.marketsRepository,
                giggyApplication().container.apolloClient.apolloStore,
                marketsViewModel,
            )
            marketCartViewModel
        }

        initializer {
            userOrdersViewModel = UserOrdersViewModel(
                giggyApplication().container.marketsRepository,
            )
            userOrdersViewModel
        }
    }
}

/* Instance of Vuno App */
fun CreationExtras.giggyApplication(): VunoApp = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VunoApp)