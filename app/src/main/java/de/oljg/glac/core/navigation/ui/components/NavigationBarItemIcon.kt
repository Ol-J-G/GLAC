package de.oljg.glac.core.navigation.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.navigation.common.GlacRoute
import de.oljg.glac.core.navigation.common.GlacScreen

@Composable
fun NavigationBarItemIcon(
    screen: GlacScreen,
    labels: Map<GlacRoute, String>,
    selected: Boolean,
) {
    val iconLabel = stringResource(id = R.string.icon)
    Icon(
        imageVector = if (selected) screen.tabIconSelected else screen.tabIconUnselected,
        contentDescription = when (screen.route) {
            GlacRoute.CLOCK_SETTINGS.name -> "${labels.getValue(GlacRoute.CLOCK_SETTINGS)} $iconLabel"
            GlacRoute.ALARM_SETTINGS.name -> "${labels.getValue(GlacRoute.ALARM_SETTINGS)} $iconLabel"
            GlacRoute.INFO_HELP.name -> "${labels.getValue(GlacRoute.INFO_HELP)} $iconLabel"
            GlacRoute.INFO_ABOUT.name -> "${labels.getValue(GlacRoute.INFO_ABOUT)} $iconLabel"
            else -> throw IllegalStateException()
        }
    )
}
