package de.oljg.glac.core.navigation.ui.navigationrail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.oljg.glac.core.navigation.common.GlacScreen
import de.oljg.glac.core.navigation.ui.components.NavigationBarItemIcon
import de.oljg.glac.core.navigation.ui.components.NavigationBarItemLabel
import de.oljg.glac.core.navigation.ui.utils.NavigationDefaults.navItemLabels
import de.oljg.glac.core.util.CommonLayoutDefaults.DEFAULT_NAVIGATION_RAIL_ITEM_SPACE

@Composable
fun GlacNavigationRail(
    navigationRailScreens: List<GlacScreen>,
    selected: (GlacScreen) -> Boolean,
    onNavigationRailItemIsClicked: (GlacScreen) -> Unit
) {
    NavigationRail {
        Column(
            modifier = Modifier
                /**
                 * Exceptionally, default BG for [NavigationRail] changed, because it plays the
                 * role of 'expanded device's equivalent' of compact device's bottom navigation,
                 * so, wanted to make it look same as (Glac)BottomNavigationBar, to indicate users,
                 * hey, it's the same sub menu, just moved to another place to be better
                 * reach-/useable. (=> adaptive design)
                 */
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                .fillMaxHeight(),
            verticalArrangement = Arrangement
                .spacedBy(DEFAULT_NAVIGATION_RAIL_ITEM_SPACE, Alignment.Bottom)
        ) {
            navigationRailScreens.forEach { screen ->
                NavigationRailItem(
                    selected = selected(screen),
                    onClick = { onNavigationRailItemIsClicked(screen) },
                    label = { NavigationBarItemLabel(screen, navItemLabels) },
                    icon = { NavigationBarItemIcon(screen, navItemLabels, selected(screen)) },
                )
            }
        }
    }
}
