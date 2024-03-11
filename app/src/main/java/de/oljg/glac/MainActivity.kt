package de.oljg.glac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.oljg.glac.core.navigation.GlacNavHost
import de.oljg.glac.core.navigation.common.ClockFullScreen
import de.oljg.glac.core.navigation.common.tabRowScreens
import de.oljg.glac.core.navigation.navigateSingleTopTo
import de.oljg.glac.core.navigation.ui.topappbar.GlacTopAppBar
import de.oljg.glac.ui.theme.GLACTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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

        // default / start screen is Fullscreen Clock
        val currentScreen = tabRowScreens.find { clacScreen ->
            clacScreen.route == currentDestination?.route
        } ?: ClockFullScreen

        Scaffold(
            topBar = {

                // Don't show topBar when fullscreen clock is displayed
                if(currentScreen is ClockFullScreen) Box {}
                else {
                    GlacTopAppBar(
                        allScreensToDisplay = tabRowScreens,
                        onTabSelected = { screen ->
                            navController.navigateSingleTopTo(screen.route)
                        },
                        currentScreen = currentScreen
                    )
                }

            }
        ) { scaffoldInnerPadding ->
            GlacNavHost(
                modifier = Modifier.padding(scaffoldInnerPadding),
                navController = navController
            )
        }
    }
}

