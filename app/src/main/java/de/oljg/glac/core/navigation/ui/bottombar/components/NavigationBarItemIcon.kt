package de.oljg.glac.core.navigation.ui.bottombar.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.navigation.common.GlacRoute
import de.oljg.glac.core.navigation.common.GlacScreen

@Composable
fun NavigationBarItemIcon(
    screen: GlacScreen,
    labels: Map<GlacRoute, String>
) {
    val iconLabel = stringResource(id = R.string.icon)
    Icon(
        imageVector = screen.tabIconSelected,
        contentDescription = when (screen.route) {
            GlacRoute.CLOCK_SETTINGS.name -> "${labels.getValue(GlacRoute.CLOCK_SETTINGS)} $iconLabel"
            GlacRoute.ALARM_SETTINGS.name -> "${labels.getValue(GlacRoute.ALARM_SETTINGS)} $iconLabel"
            GlacRoute.COMMON_SETTINGS.name -> "${labels.getValue(GlacRoute.COMMON_SETTINGS)} $iconLabel"
            else -> throw IllegalStateException()
        }
    )
}
