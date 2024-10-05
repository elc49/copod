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
import com.lomolo.copod.compose.screens.AllMarketsViewModel
import com.lomolo.copod.compose.screens.CartViewModel
import com.lomolo.copod.compose.screens.CreateFarmViewModel
import com.lomolo.copod.compose.screens.FarmOrderViewModel
import com.lomolo.copod.compose.screens.FarmProfileViewModel
import com.lomolo.copod.compose.screens.FarmSettingsViewModel
import com.lomolo.copod.compose.screens.FarmStoreViewModel
import com.lomolo.copod.compose.screens.FarmViewModel
import com.lomolo.copod.compose.screens.MachineryViewModel
import com.lomolo.copod.compose.screens.MarketDetailsViewModel
import com.lomolo.copod.compose.screens.MarketsViewModel
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
        lateinit var cartViewModel: CartViewModel
        lateinit var userOrdersViewModel: UserOrdersViewModel
        lateinit var farmSettingsViewModel: FarmSettingsViewModel
        lateinit var marketDetailsViewModel: MarketDetailsViewModel
        lateinit var bottomNavBarViewModel: BottomNavBarViewModel
        lateinit var seedsViewModel: SeedsViewModel
        lateinit var seedlingsViewModel: SeedlingsViewModel
        lateinit var machineryViewModel: MachineryViewModel
        lateinit var farmProfileViewModel: FarmProfileViewModel
        lateinit var allMarketsViewModel: AllMarketsViewModel
        lateinit var farmOrderViewModel: FarmOrderViewModel
        lateinit var paystackViewModel: PaystackViewModel

        initializer {
            mainViewModel = MainViewModel(copodApplication().container.copodRestApiService)
            mainViewModel
        }

        initializer {
            sessionViewModel = SessionViewModel(
                copodApplication().container.sessionRepository,
                copodApplication().container.paymentRepository,
                copodApplication().container.apolloClient.apolloStore,
                copodApplication().container.copodGraphqlApiService,
            )
            sessionViewModel
        }

        initializer {
            storeViewModel = FarmViewModel(
                copodApplication().container.farmRepository,
            )
            storeViewModel
        }

        initializer {
            farmStoreViewModel = FarmStoreViewModel(
                this.createSavedStateHandle(),
                copodApplication().container.copodGraphqlApiService,
            )
            farmStoreViewModel
        }

        initializer {
            addFarmMarketViewModel = AddFarmMarketViewModel(
                copodApplication().container.copodRestApiService,
                farmStoreViewModel,
                copodApplication().container.copodGraphqlApiService,
                copodApplication().container.apolloClient.apolloStore,
                mainViewModel,
            )
            addFarmMarketViewModel
        }

        initializer {
            createFarmViewModel = CreateFarmViewModel(
                copodApplication().container.apolloClient.apolloStore,
                copodApplication().container.copodRestApiService,
                copodApplication().container.farmRepository,
                mainViewModel,
            )
            createFarmViewModel
        }

        initializer {
            accountViewModel = AccountViewModel(
                copodApplication().container.copodGraphqlApiService,
            )
            accountViewModel
        }

        initializer {
            signinViewModel = SigninViewModel(
                copodApplication().container.sessionRepository, mainViewModel,
                sessionViewModel,
            )
            signinViewModel
        }

        initializer {
            marketsViewModel = MarketsViewModel(
                copodApplication().container.marketsRepository,
                mainViewModel,
            )
            marketsViewModel
        }

        initializer {
            cartViewModel = CartViewModel(
                copodApplication().container.marketsRepository,
                copodApplication().container.apolloClient.apolloStore,
            )
            cartViewModel
        }

        initializer {
            userOrdersViewModel = UserOrdersViewModel(
                copodApplication().container.marketsRepository,
            )
            userOrdersViewModel
        }

        initializer {
            farmSettingsViewModel = FarmSettingsViewModel(
                copodApplication().container.copodGraphqlApiService,
                copodApplication().container.copodRestApiService,
                this.createSavedStateHandle(),
                copodApplication().container.apolloClient.apolloStore,
            )
            farmSettingsViewModel
        }

        initializer {
            marketDetailsViewModel = MarketDetailsViewModel(
                this.createSavedStateHandle(),
                copodApplication().container.marketsRepository,
                copodApplication().container.apolloClient.apolloStore,
            )
            marketDetailsViewModel
        }

        initializer {
            bottomNavBarViewModel = BottomNavBarViewModel(
                copodApplication().container.apolloClient.apolloStore,
            )
            bottomNavBarViewModel
        }

        initializer {
            seedsViewModel = SeedsViewModel(
                copodApplication().container.marketsRepository, mainViewModel
            )
            seedsViewModel
        }

        initializer {
            seedlingsViewModel = SeedlingsViewModel(
                copodApplication().container.marketsRepository,
                mainViewModel = mainViewModel,
            )
            seedlingsViewModel
        }

        initializer {
            machineryViewModel = MachineryViewModel(
                copodApplication().container.marketsRepository,
                mainViewModel = mainViewModel,
            )
            machineryViewModel
        }

        initializer {
            farmProfileViewModel = FarmProfileViewModel(
                copodApplication().container.farmRepository,
                copodApplication().container.marketsRepository,
                this.createSavedStateHandle(),
            )
            farmProfileViewModel
        }

        initializer {
            allMarketsViewModel = AllMarketsViewModel(
                copodApplication().container.farmRepository,
                this.createSavedStateHandle(),
            )
            allMarketsViewModel
        }

        initializer {
            farmOrderViewModel = FarmOrderViewModel(
                copodApplication().container.copodGraphqlApiService,
                this.createSavedStateHandle(),
                copodApplication().container.apolloClient.apolloStore,
            )
            farmOrderViewModel
        }

        initializer {
            paystackViewModel = PaystackViewModel(
                copodApplication().container.paymentRepository,
                mainViewModel,
                sessionViewModel,
            )
            paystackViewModel
        }
    }
}

/* Instance of Vuno App */
fun CreationExtras.copodApplication(): CopodApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CopodApp)