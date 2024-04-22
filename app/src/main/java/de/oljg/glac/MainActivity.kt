package de.oljg.glac

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.oljg.glac.core.navigation.GlacNavHost
import de.oljg.glac.core.navigation.common.ClockFullScreen
import de.oljg.glac.core.navigation.common.ClockSettingsScreen
import de.oljg.glac.core.navigation.common.SettingsScreen
import de.oljg.glac.core.navigation.common.glacSettingsSubScreens
import de.oljg.glac.core.navigation.common.allGlacScreens
import de.oljg.glac.core.navigation.common.glacTabScreens
import de.oljg.glac.core.navigation.common.isSettingsSubScreen
import de.oljg.glac.core.navigation.navigateSingleTopTo
import de.oljg.glac.core.navigation.ui.bottombar.GlacBottomNavigationBar
import de.oljg.glac.core.navigation.ui.topappbar.GlacTabBar
import de.oljg.glac.ui.theme.GLACTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlacApp()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GlacApp() {
    GLACTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination

        // Default / Start screen is fullscreen clock
        val currentScreen = allGlacScreens.find { glacScreen ->
            glacScreen.route == currentDestination?.route
        } ?: ClockFullScreen

        var currentSubSettingsScreenRoute by rememberSaveable {
            mutableStateOf(ClockSettingsScreen.route)
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {

                    // Don't show topBar when fullscreen clock is displayed
                    if (currentScreen !is ClockFullScreen) {
                        GlacTabBar(
                            tabBarScreens = glacTabScreens,
                            onTabSelected = { tabBarScreen ->
                                when (tabBarScreen) {
                                    /**
                                     * Navigate to the currently/lastly selected sub settings
                                     * screen, when settings tab is selected.
                                     */
                                    SettingsScreen ->
                                        navController.navigateSingleTopTo(
                                            currentSubSettingsScreenRoute
                                        )

                                    else -> navController.navigateSingleTopTo(tabBarScreen.route)
                                }
                            },

                            /**
                             * Let settings tab stay selected when one of the bottom bar's tabs
                             * (sub settings) are selected.
                             */
                            currentScreen = if (currentScreen.isSettingsSubScreen()) SettingsScreen
                            else currentScreen
                        )
                    }
                },
                bottomBar = {
                    /**
                     * Show bottom navigation bar only in case a settings screen is selected.
                     */
                    if (currentScreen is SettingsScreen || currentScreen.isSettingsSubScreen()) {
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
                }
            ) { scaffoldInnerPadding ->
                GlacNavHost(
                    modifier =
                    if (currentScreen is ClockFullScreen) Modifier.padding(0.dp)
                    else Modifier.padding(scaffoldInnerPadding),
                    navController = navController,
                )
            }
        }
    }
}

