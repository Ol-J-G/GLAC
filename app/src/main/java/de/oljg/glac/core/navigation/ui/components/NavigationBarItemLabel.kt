package de.oljg.glac.core.navigation.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.oljg.glac.core.navigation.common.GlacRoute
import de.oljg.glac.core.navigation.common.GlacScreen

@Composable
fun NavigationBarItemLabel(
    screen: GlacScreen,
    labels: Map<GlacRoute, String>
) {
    Text(
        text = when (screen.route) {
            GlacRoute.CLOCK_SETTINGS.name -> labels.getValue(GlacRoute.CLOCK_SETTINGS)
            GlacRoute.ALARM_SETTINGS.name -> labels.getValue(GlacRoute.ALARM_SETTINGS)
            GlacRoute.INFO_HELP.name -> labels.getValue(GlacRoute.INFO_HELP)
            GlacRoute.INFO_ABOUT.name -> labels.getValue(GlacRoute.INFO_ABOUT)
            else -> throw IllegalStateException()
        }
    )
}
