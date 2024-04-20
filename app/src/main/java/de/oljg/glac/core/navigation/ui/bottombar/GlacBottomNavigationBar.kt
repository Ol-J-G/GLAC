package de.oljg.glac.core.navigation.ui.bottombar

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.navigation.common.GlacRoute
import de.oljg.glac.core.navigation.common.GlacScreen
import de.oljg.glac.core.navigation.ui.bottombar.components.NavigationBarItemIcon
import de.oljg.glac.core.navigation.ui.bottombar.components.NavigationBarItemLabel

@Composable
fun GlacBottomNavigationBar(
    navigationBarItems: List<GlacScreen>,
    selected: (GlacScreen) -> Boolean,
    onNavigationBarItemIsClicked: (GlacScreen) -> Unit
) {
    NavigationBar {
        val navItemlabels = mapOf(
            Pair(GlacRoute.CLOCK_SETTINGS, stringResource(id = R.string.clock_settings)),
            Pair(GlacRoute.ALARM_SETTINGS, stringResource(id = R.string.alarm_settings)),
            Pair(GlacRoute.COMMON_SETTINGS, stringResource(id = R.string.common_settings))
        )
        navigationBarItems.forEach { screen ->
            NavigationBarItem(
                selected = selected(screen),
                onClick = { onNavigationBarItemIsClicked(screen) },
                label = { NavigationBarItemLabel(screen, navItemlabels) },
                icon = { NavigationBarItemIcon(screen, navItemlabels) }
            )
        }
    }
}

