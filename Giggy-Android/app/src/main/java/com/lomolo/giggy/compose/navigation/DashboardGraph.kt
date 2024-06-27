package com.lomolo.giggy.compose.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.lomolo.giggy.R
import com.lomolo.giggy.SessionViewModel
import com.lomolo.giggy.compose.screens.AccountScreen
import com.lomolo.giggy.compose.screens.AccountScreenDestination
import com.lomolo.giggy.compose.screens.CreateFarmMarketDestination
import com.lomolo.giggy.compose.screens.CreateFarmMarketScreen
import com.lomolo.giggy.compose.screens.CreateFarmScreen
import com.lomolo.giggy.compose.screens.CreateFarmScreenDestination
import com.lomolo.giggy.compose.screens.CreatePostScreen
import com.lomolo.giggy.compose.screens.CreatePostScreenDestination
import com.lomolo.giggy.compose.screens.DashboardScreen
import com.lomolo.giggy.compose.screens.DashboardScreenDestination
import com.lomolo.giggy.compose.screens.FarmMarketScreen
import com.lomolo.giggy.compose.screens.FarmMarketScreenDestination
import com.lomolo.giggy.compose.screens.FarmScreenDestination
import com.lomolo.giggy.compose.screens.FarmsScreen
import com.lomolo.giggy.compose.screens.MarketScreen
import com.lomolo.giggy.compose.screens.MarketScreenDestination
import com.lomolo.giggy.model.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object DashboardDestination : Navigation {
    override val title = null
    override val route = "dashboard"
}

sealed class Screen(
    val name: String,
    val defaultIcon: Int,
    val activeIcon: Int,
    val route: String,
) {
    data object Explore : Screen(
        "Explore",
        R.drawable.explore_outlined,
        R.drawable.explore_filled,
        "dashboard-home",
    )

    data object Soko : Screen(
        "Soko",
        R.drawable.cart_outlined,
        R.drawable.cart_filled,
        "dashboard-market",
    )

    data object Farm : Screen(
        "Farm",
        R.drawable.farm_outlined,
        R.drawable.farm_filled,
        "dashboard-farm",
    )

    data object Account : Screen(
        "You",
        R.drawable.account_outlined,
        R.drawable.account_filled,
        "dashboard-account",
    )
}

@Composable
internal fun BottomNavBar(
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit = {},
    currentDestination: NavDestination?,
) {
    val navItems = listOf(Screen.Explore, Screen.Soko, Screen.Farm, Screen.Account)

    NavigationBar(
        modifier = modifier,
    ) {
        navItems.forEachIndexed { _, item ->
            val isNavItemActive =
                currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(selected = isNavItemActive, onClick = {
                onNavigateTo(item.route)
            }, icon = {
                Icon(
                    painterResource(if (isNavItemActive) item.activeIcon else item.defaultIcon),
                    modifier = Modifier.size(32.dp),
                    contentDescription = item.name
                )
            }, label = {
                Text(
                    item.name,
                    fontWeight = if (isNavItemActive) FontWeight.ExtraBold
                    else FontWeight.Normal,
                )
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.addDashboardGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    sessionViewModel: SessionViewModel,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    session: Session,
) {
    val onNavigateTo = { route: String ->
        navHostController.navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = false
            }
            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = true
            // Restore state when re-selecting a previously selected item
            restoreState = true
        }
    }

    navigation(
        startDestination = DashboardScreenDestination.route,
        route = DashboardDestination.route,
    ) {
        composable(route = DashboardScreenDestination.route) {
            val currentDestination = it.destination

            Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, floatingActionButton = {
                IconButton(modifier = Modifier.background(
                    MaterialTheme.colorScheme.primary,
                    CircleShape,
                ), onClick = {
                    navHostController.navigate(CreatePostScreenDestination.route) {
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        Icons.TwoTone.Add,
                        tint = MaterialTheme.colorScheme.background,
                        contentDescription = stringResource(R.string.create_post),
                    )
                }
            }, bottomBar = {
                BottomNavBar(
                    modifier = modifier,
                    onNavigateTo = onNavigateTo,
                    currentDestination = currentDestination,
                )
            }) { innerPadding ->
                Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    DashboardScreen()
                }
            }
        }
        composable(route = MarketScreenDestination.route) {
            MarketScreen(
                bottomNav = {
                    BottomNavBar(
                        modifier = modifier,
                        onNavigateTo = onNavigateTo,
                        currentDestination = it.destination
                    )
                }
            )
        }
        composable(route = FarmScreenDestination.route) {
            val currentDestination = it.destination

            FarmsScreen(
                snackbarHostState = snackbarHostState,
                onNavigateToFarmMarket = { farmId ->
                    navHostController.navigate("${FarmMarketScreenDestination.route}/${farmId}")
                },
                onNavigateToCreateFarm = {
                    navHostController.navigate(CreateFarmScreenDestination.route)
                },
                bottomNav = {
                    BottomNavBar(
                        modifier = modifier,
                        currentDestination = currentDestination,
                        onNavigateTo = onNavigateTo,
                    )
                }
            )
        }
        composable(
            route = FarmMarketScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(FarmMarketScreenDestination.farmIdArg) {
                type = NavType.StringType
            })
        ) {
            Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
                TopAppBar(title = {
                    Text(
                        stringResource(id = R.string.farm),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }, navigationIcon = {
                    IconButton(onClick = {
                        navHostController.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.TwoTone.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                })
            }) {
                Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    FarmMarketScreen(onCreateFarmMarket = {
                        navHostController.navigate(CreateFarmMarketDestination.route)
                    })
                }
            }
        }
        composable(route = AccountScreenDestination.route) {
            AccountScreen(
                bottomNav = {
                    BottomNavBar(
                        modifier = modifier,
                        onNavigateTo = onNavigateTo,
                        currentDestination = it.destination,
                    )
                },
                onSignOut = {
                    sessionViewModel.signOut {
                        navHostController.navigate(HomeDestination.route) {
                            popUpTo(HomeDestination.route) {
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
            )
        }
        dialog(
            route = CreatePostScreenDestination.route,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            CreatePostScreen(
                onCloseDialog = {
                    navHostController.popBackStack()
                },
                showToast = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Post created", withDismissAction = true)
                    }
                },
                session = session,
            )
        }
        dialog(
            route = CreateFarmScreenDestination.route,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Scaffold(topBar = {
                TopAppBar(title = {
                    Text(
                        stringResource(id = CreateFarmScreenDestination.title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }, navigationIcon = {
                    IconButton(onClick = {
                        navHostController.popBackStack()
                    }) {
                        Icon(
                            Icons.TwoTone.Close, contentDescription = null
                        )
                    }
                })
            }) { innerPadding ->
                Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CreateFarmScreen(
                        onNavigateBack = {
                            navHostController.popBackStack()
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Farm created.", withDismissAction = true
                                )
                            }
                        },
                    )
                }
            }
        }
        dialog(
            route = CreateFarmMarketDestination.route,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            CreateFarmMarketScreen(onGoBack = {
                navHostController.popBackStack()
            }, showToast = {
                scope.launch {
                    snackbarHostState.showSnackbar("Market created.", withDismissAction = true)
                }
            })
        }
    }
}

