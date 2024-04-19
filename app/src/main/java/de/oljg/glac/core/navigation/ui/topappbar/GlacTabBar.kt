package de.oljg.glac.core.navigation.ui.topappbar


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.navigation.common.GlacRoute
import de.oljg.glac.core.navigation.common.GlacScreen
import de.oljg.glac.core.navigation.ui.topappbar.components.GlacTab
import de.oljg.glac.core.navigation.ui.topappbar.util.Constants.TOP_APP_BAR_HEIGHT

@Composable
fun GlacTabBar(
    allScreensToDisplay: List<GlacScreen>,
    onTabSelected: (GlacScreen) -> Unit,
    currentScreen: GlacScreen
) {
    Surface(
        Modifier
            .height(TOP_APP_BAR_HEIGHT)
            .fillMaxWidth()
    ) {
        Row(Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {

            allScreensToDisplay.forEach { screen ->
                GlacTab(
                    tabText = when(screen.route) {
                        GlacRoute.CLOCK.name -> stringResource(R.string.clock)
                        GlacRoute.ALARMS.name -> stringResource(id = R.string.alarms)
                        GlacRoute.SETTINGS.name -> stringResource(R.string.settings)
                        GlacRoute.ABOUT.name -> stringResource(R.string.about)
                        else -> ""
                    },
                    tabIconFilled = screen.tabIconSelected,
                    tabIconOutlined = screen.tabIconUnselected,
                    onSelected = { onTabSelected(screen) },
                    tabIsSelected = currentScreen == screen
                )
            }
        }
    }
}