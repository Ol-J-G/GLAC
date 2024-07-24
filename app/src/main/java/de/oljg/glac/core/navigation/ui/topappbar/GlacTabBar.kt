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
import de.oljg.glac.core.utils.TestTags.ALARMS_TAB
import de.oljg.glac.core.utils.TestTags.CLOCK_TAB
import de.oljg.glac.core.utils.TestTags.INFO_TAB
import de.oljg.glac.core.utils.TestTags.SETTINGS_TAB

@Composable
fun GlacTabBar(
    tabBarScreens: List<GlacScreen>,
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

            tabBarScreens.forEach { screen ->
                GlacTab(
                    tabText = when(screen.route) {
                        GlacRoute.CLOCK.name -> stringResource(R.string.clock)
                        GlacRoute.ALARMS.name -> stringResource(id = R.string.alarms)
                        GlacRoute.SETTINGS.name -> stringResource(R.string.settings)
                        GlacRoute.INFO.name -> stringResource(R.string.info)
                        else -> throw IllegalStateException()
                    },
                    tabIconFilled = screen.tabIconSelected,
                    tabIconOutlined = screen.tabIconUnselected,
                    onSelected = { onTabSelected(screen) },
                    tabIsSelected = currentScreen == screen,
                    testTag = when(screen.route) {
                        GlacRoute.CLOCK.name -> CLOCK_TAB
                        GlacRoute.ALARMS.name -> ALARMS_TAB
                        GlacRoute.SETTINGS.name -> SETTINGS_TAB
                        GlacRoute.INFO.name -> INFO_TAB
                        else -> throw IllegalStateException()
                    }
                )
            }
        }
    }
}
