package de.oljg.glac

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import de.oljg.glac.core.navigation.common.settingsSubScreens
import de.oljg.glac.core.navigation.common.glacScreens
import de.oljg.glac.core.navigation.common.glacTabScreens
import de.oljg.glac.core.navigation.common.isSettingsSubScreen
import de.oljg.glac.core.navigation.navigateSingleTopTo
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

        // default / start screen is Fullscreen Clock
        val currentScreen = glacScreens.find { clacScreen ->
            clacScreen.route == currentDestination?.route
        } ?: ClockFullScreen

        var selectedBottomBarItemIndex by rememberSaveable {
            mutableIntStateOf(0)
        }
        var currentSelectedSubSettingsScreenRoute by rememberSaveable {
            mutableStateOf(ClockSettingsScreen.route)
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {

                    // Don't show topBar when fullscreen clock is displayed
                    if (currentScreen !is ClockFullScreen)
                        GlacTabBar(
                            allScreensToDisplay = glacTabScreens,
                            onTabSelected = { screen ->
                                when (screen) {

                                    /**
                                     * Navigate to the currently selected sub settings screen,
                                     * when selecting settings tab.
                                     */
                                    SettingsScreen ->
                                        navController.navigateSingleTopTo(
                                            currentSelectedSubSettingsScreenRoute
                                        )

                                    else -> navController.navigateSingleTopTo(screen.route)
                                }
                            },

                            /**
                             * Let settings tab stay selected when one of the bottom bar's tabs
                             * (sub settings) are selected.
                             */
                            currentScreen =
                                if(currentScreen.isSettingsSubScreen()) SettingsScreen
                                else currentScreen
                        )
                },
                bottomBar = {
                    if (currentScreen is SettingsScreen ||
                        currentScreen.isSettingsSubScreen()
                    ) {
                        NavigationBar {
                            settingsSubScreens.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedBottomBarItemIndex == index,
                                    onClick = {
                                        selectedBottomBarItemIndex = index
                                        currentSelectedSubSettingsScreenRoute = item.route
                                        navController.navigateSingleTopTo(item.route)
                                    },
                                    label = { Text(text = item.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selectedBottomBarItemIndex == index)
                                                item.tabIconSelected else item.tabIconUnselected,
                                            contentDescription = null //TODO: add content desc to GlacScreen
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            ) { scaffoldInnerPadding ->
                GlacNavHost(
                    modifier =
                    if (currentScreen is ClockFullScreen) Modifier.padding(0.dp)
                    else
                        Modifier.padding(scaffoldInnerPadding),
                    navController = navController,
                )
            }
        }
    }
}

