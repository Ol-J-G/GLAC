package de.oljg.glac

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.oljg.glac.core.navigation.GlacNavHost
import de.oljg.glac.core.navigation.common.ClockFullScreen
import de.oljg.glac.core.navigation.common.tabRowScreens
import de.oljg.glac.core.navigation.navigateSingleTopTo
import de.oljg.glac.core.navigation.ui.topappbar.GlacTopAppBar
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
        val currentScreen = tabRowScreens.find { clacScreen ->
            clacScreen.route == currentDestination?.route
        } ?: ClockFullScreen

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {

                    // Don't show topBar when fullscreen clock is displayed
                    if (currentScreen is ClockFullScreen) Box {}
                    else GlacTopAppBar(
                        allScreensToDisplay = tabRowScreens,
                        onTabSelected = { screen ->
                            navController.navigateSingleTopTo(screen.route)
                        },
                        currentScreen = currentScreen
                    )
                }
            ) { scaffoldInnerPadding ->
                GlacNavHost(
                    modifier =
                    if (currentScreen is ClockFullScreen) Modifier.padding(0.dp)
                    else
                        Modifier.padding(scaffoldInnerPadding),
                    navController = navController
                )
            }
        }
    }
}

