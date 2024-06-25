package de.oljg.glac.core.navigation.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.navigation.common.GlacRoute

object NavigationDefaults {
    val navItemLabels: Map<GlacRoute, String> @Composable get() = mapOf(
        Pair(GlacRoute.CLOCK_SETTINGS, stringResource(id = R.string.clock)),
        Pair(GlacRoute.ALARM_SETTINGS, stringResource(id = R.string.alarm)),
        Pair(GlacRoute.COMMON_SETTINGS, stringResource(id = R.string.common))
    )
}


