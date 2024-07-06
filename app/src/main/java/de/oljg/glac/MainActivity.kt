package de.oljg.glac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import de.oljg.glac.core.navigation.GlacNavHost
import de.oljg.glac.core.navigation.common.AlarmClockFullScreen
import de.oljg.glac.core.navigation.common.ClockSettingsSubScreen
import de.oljg.glac.core.navigation.common.HelpSubScreen
import de.oljg.glac.core.navigation.common.InfoScreen
import de.oljg.glac.core.navigation.common.SettingsScreen
import de.oljg.glac.core.navigation.common.allGlacScreens
import de.oljg.glac.core.navigation.common.glacInfoSubScreens
import de.oljg.glac.core.navigation.common.glacSettingsSubScreens
import de.oljg.glac.core.navigation.common.glacTabScreens
import de.oljg.glac.core.navigation.common.isInfoScreen
import de.oljg.glac.core.navigation.common.isInfoSubScreen
import de.oljg.glac.core.navigation.common.isSettingsScreen
import de.oljg.glac.core.navigation.common.isSettingsSubScreen
import de.oljg.glac.core.navigation.navigateSingleTopTo
import de.oljg.glac.core.navigation.ui.bottombar.GlacBottomNavigationBar
import de.oljg.glac.core.navigation.ui.navigationrail.GlacNavigationRail
import de.oljg.glac.core.navigation.ui.topappbar.GlacTabBar
import de.oljg.glac.core.util.CommonLayoutDefaults.DEFAULT_NAVIGATION_RAIL_WIDTH
import de.oljg.glac.core.util.ScreenDetails
import de.oljg.glac.core.util.screenDetails
import de.oljg.glac.feature_alarm.ui.AlarmSettingsEvent
import de.oljg.glac.feature_alarm.ui.AlarmSettingsViewModel
import de.oljg.glac.ui.theme.GLACTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /**
             * Re-schedule all alarms once on app start (instead of on device boot completed).
             *
             * Note that I tried to re-schedule after device boot phase, with BroadcastReceiver
             * (see: https://github.com/philipplackner/AutoStartAndroid), but no chance to
             * get list of alarms out of a Flow, outside of a Composable, and there seems to be
             * no other way to get the data from datastore (also, the naive idea to read the
             * JSON file from inside BroadcastReceiver will end in a SecurityException ..:D).
             *
             * Alternatively, introducing Room DB just for a couple of alarms (I assume, the
             * majority of users will not schedule more than 10 alarms!), seems too much
             * effort for too little benefit imho (but however, accessing viewmodel from
             * BroadcastReceiver and call a DB-repo-reScheduleAll fun should work at all...)
             */
            var alarmsHaveRecentlyBeenRescheduled by rememberSaveable {
                mutableStateOf(false)
            }
            val viewModel: AlarmSettingsViewModel = hiltViewModel()

            if (!alarmsHaveRecentlyBeenRescheduled) {
                viewModel.onEvent(AlarmSettingsEvent.ReScheduleAllAlarms)
                alarmsHaveRecentlyBeenRescheduled = true
            }
            GlacApp()
        }
    }
}


@Composable
fun GlacApp() {
    GLACTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination

        // Default / Start screen is fullscreen clock
        val currentScreen = allGlacScreens.find { glacScreen ->
            glacScreen.route == currentDestination?.route
        } ?: AlarmClockFullScreen

        var currentSubSettingsScreenRoute by rememberSaveable {
            mutableStateOf(ClockSettingsSubScreen.route)
        }
        var currentSubInfoScreenRoute by rememberSaveable {
            mutableStateOf(HelpSubScreen.route)
        }

        // Basically, show navigation rail (side bar) only when screen width is medium or expanded
        val showNavigationRail =
                screenDetails().screenWidthType !is ScreenDetails.DisplayType.Compact

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    // Don't show topBar when fullscreen clock is displayed
                    if (currentScreen !is AlarmClockFullScreen) {
                        GlacTabBar(
                            tabBarScreens = glacTabScreens,
                            onTabSelected = { tabBarScreen ->
                                /**
                                 * Navigate to the currently/lastly selected sub settings / sub info
                                 * screen, when settings / info tab is selected.
                                 */
                                when (tabBarScreen) {
                                    SettingsScreen ->
                                        navController.navigateSingleTopTo(
                                            currentSubSettingsScreenRoute
                                        )

                                    InfoScreen ->
                                        navController.navigateSingleTopTo(
                                            currentSubInfoScreenRoute
                                        )

                                    else -> navController.navigateSingleTopTo(tabBarScreen.route)
                                }
                            },

                            /**
                             * Let settings / info tab stay selected when one of the bottom bar's /
                             * navigation rail's tabs (sub settings / sub info) are selected.
                             */
                            currentScreen = when {
                                currentScreen.isSettingsSubScreen() -> SettingsScreen
                                currentScreen.isInfoSubScreen() -> InfoScreen
                                else -> currentScreen
                            }
                        )
                    }
                },
                bottomBar = {
                    /**
                     * Show bottom navigation bar only in case a settings or info screen is
                     * selected and screen width is compact.
                     */
                    when {
                        !showNavigationRail && currentScreen.isSettingsScreen() -> {
                            GlacBottomNavigationBar(
                                bottomNavigationBarScreens = glacSettingsSubScreens,
                                selected = { screen ->
                                    currentSubSettingsScreenRoute == screen.route
                                },
                                onNavigationBarItemIsClicked = { screen ->
                                    currentSubSettingsScreenRoute = screen.route
                                    navController.navigateSingleTopTo(screen.route)
                                }
                            )
                        }

                        !showNavigationRail && currentScreen.isInfoSubScreen() -> {
                            GlacBottomNavigationBar(
                                bottomNavigationBarScreens = glacInfoSubScreens,
                                selected = { screen ->
                                    currentSubInfoScreenRoute == screen.route
                                },
                                onNavigationBarItemIsClicked = { screen ->
                                    currentSubInfoScreenRoute = screen.route
                                    navController.navigateSingleTopTo(screen.route)
                                }
                            )
                        }
                    }
                }
            ) { scaffoldInnerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            if (currentScreen is AlarmClockFullScreen)
                                PaddingValues(0.dp) else scaffoldInnerPadding
                        )
                        .padding(
                            start = if (showNavigationRail
                                && (currentScreen.isSettingsScreen()
                                        || currentScreen.isInfoScreen()))
                                DEFAULT_NAVIGATION_RAIL_WIDTH else 0.dp
                        )
                ) {
                    GlacNavHost(navController = navController)
                }
            }
        }

        /**
         * Show navigation rail only in case a settings or info screen is
         * selected and screen width is not compact.
         */
        when {
            showNavigationRail && currentScreen.isSettingsScreen() -> {
                GlacNavigationRail(
                    navigationRailScreens = glacSettingsSubScreens,
                    selected = { screen ->
                        currentSubSettingsScreenRoute == screen.route
                    },
                    onNavigationRailItemIsClicked = { screen ->
                        currentSubSettingsScreenRoute = screen.route
                        navController.navigateSingleTopTo(screen.route)
                    }
                )
            }

            showNavigationRail && currentScreen.isInfoSubScreen() -> {
                GlacNavigationRail(
                    navigationRailScreens = glacInfoSubScreens,
                    selected = { screen ->
                        currentSubInfoScreenRoute == screen.route
                    },
                    onNavigationRailItemIsClicked = { screen ->
                        currentSubInfoScreenRoute = screen.route
                        navController.navigateSingleTopTo(screen.route)
                    }
                )
            }
        }
    }
}
