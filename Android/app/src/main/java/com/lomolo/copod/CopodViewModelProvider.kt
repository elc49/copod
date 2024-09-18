package com.lomolo.copod

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.apollographql.apollo3.cache.normalized.apolloStore
import com.lomolo.copod.common.BottomNavBarViewModel
import com.lomolo.copod.compose.screens.AccountViewModel
import com.lomolo.copod.compose.screens.AddFarmMarketViewModel
import com.lomolo.copod.compose.screens.CartViewModel
import com.lomolo.copod.compose.screens.CreateFarmViewModel
import com.lomolo.copod.compose.screens.FarmProfileViewModel
import com.lomolo.copod.compose.screens.FarmSettingsViewModel
import com.lomolo.copod.compose.screens.FarmStoreViewModel
import com.lomolo.copod.compose.screens.FarmViewModel
import com.lomolo.copod.compose.screens.MachineryViewModel
import com.lomolo.copod.compose.screens.MarketDetailsViewModel
import com.lomolo.copod.compose.screens.MarketsViewModel
import com.lomolo.copod.compose.screens.PaymentViewModel
import com.lomolo.copod.compose.screens.SeedlingsViewModel
import com.lomolo.copod.compose.screens.SeedsViewModel
import com.lomolo.copod.compose.screens.SigninViewModel
import com.lomolo.copod.compose.screens.UserOrdersViewModel

object CopodViewModelProvider {
    val Factory = viewModelFactory {
        lateinit var mainViewModel: MainViewModel
        lateinit var sessionViewModel: SessionViewModel
        lateinit var storeViewModel: FarmViewModel
        lateinit var addFarmMarketViewModel: AddFarmMarketViewModel
        lateinit var farmStoreViewModel: FarmStoreViewModel
        lateinit var createFarmViewModel: CreateFarmViewModel
        lateinit var accountViewModel: AccountViewModel
        lateinit var signinViewModel: SigninViewModel
        lateinit var marketsViewModel: MarketsViewModel
        lateinit var paymentViewModel: PaymentViewModel
        lateinit var cartViewModel: CartViewModel
        lateinit var userOrdersViewModel: UserOrdersViewModel
        lateinit var farmSettingsViewModel: FarmSettingsViewModel
        lateinit var marketDetailsViewModel: MarketDetailsViewModel
        lateinit var bottomNavBarViewModel: BottomNavBarViewModel
        lateinit var seedsViewModel: SeedsViewModel
        lateinit var seedlingsViewModel: SeedlingsViewModel
        lateinit var machineryViewModel: MachineryViewModel
        lateinit var farmProfileViewModel: FarmProfileViewModel

        initializer {
            mainViewModel = MainViewModel(vunoApplication().container.copodRestApiService)
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
            storeViewModel = FarmViewModel(
                vunoApplication().container.farmRepository,
            )
            storeViewModel
        }

        initializer {
            farmStoreViewModel = FarmStoreViewModel(
                this.createSavedStateHandle(),
                vunoApplication().container.copodGraphqlApiService,
                vunoApplication().container.apolloClient.apolloStore,
            )
            farmStoreViewModel
        }

        initializer {
            addFarmMarketViewModel = AddFarmMarketViewModel(
                vunoApplication().container.copodRestApiService,
                farmStoreViewModel,
                vunoApplication().container.copodGraphqlApiService,
                vunoApplication().container.apolloClient.apolloStore,
                mainViewModel,
            )
            addFarmMarketViewModel
        }

        initializer {
            createFarmViewModel = CreateFarmViewModel(
                vunoApplication().container.apolloClient.apolloStore,
                vunoApplication().container.copodRestApiService,
                vunoApplication().container.farmRepository,
            )
            createFarmViewModel
        }

        initializer {
            accountViewModel = AccountViewModel(
                vunoApplication().container.copodGraphqlApiService,
            )
            accountViewModel
        }

        initializer {
            signinViewModel = SigninViewModel(
                vunoApplication().container.sessionRepository, mainViewModel
            )
            signinViewModel
        }

        initializer {
            marketsViewModel = MarketsViewModel(
                vunoApplication().container.marketsRepository,
                mainViewModel,
            )
            marketsViewModel
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
            cartViewModel = CartViewModel(
                vunoApplication().container.marketsRepository,
                vunoApplication().container.apolloClient.apolloStore,
            )
            cartViewModel
        }

        initializer {
            userOrdersViewModel = UserOrdersViewModel(
                vunoApplication().container.marketsRepository,
            )
            userOrdersViewModel
        }

        initializer {
            farmSettingsViewModel = FarmSettingsViewModel(
                vunoApplication().container.copodGraphqlApiService,
                vunoApplication().container.copodRestApiService,
                this.createSavedStateHandle(),
                vunoApplication().container.apolloClient.apolloStore,
            )
            farmSettingsViewModel
        }

        initializer {
            marketDetailsViewModel = MarketDetailsViewModel(
                this.createSavedStateHandle(),
                vunoApplication().container.marketsRepository,
                vunoApplication().container.apolloClient.apolloStore,
            )
            marketDetailsViewModel
        }

        initializer {
            bottomNavBarViewModel = BottomNavBarViewModel(
                vunoApplication().container.apolloClient.apolloStore,
            )
            bottomNavBarViewModel
        }

        initializer {
            seedsViewModel = SeedsViewModel(
                vunoApplication().container.marketsRepository, mainViewModel
            )
            seedsViewModel
        }

        initializer {
            seedlingsViewModel = SeedlingsViewModel(
                vunoApplication().container.marketsRepository,
                mainViewModel = mainViewModel,
            )
            seedlingsViewModel
        }

        initializer {
            machineryViewModel = MachineryViewModel(
                vunoApplication().container.marketsRepository,
                mainViewModel = mainViewModel,
            )
            machineryViewModel
        }

        initializer {
            farmProfileViewModel = FarmProfileViewModel(
                vunoApplication().container.farmRepository,
                vunoApplication().container.marketsRepository,
            )
            farmProfileViewModel
        }
    }
}

/* Instance of Vuno App */
fun CreationExtras.vunoApplication(): CopodApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CopodApp)