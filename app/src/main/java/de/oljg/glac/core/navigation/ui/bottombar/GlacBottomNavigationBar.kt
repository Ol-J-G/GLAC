package de.oljg.glac.core.navigation.ui.bottombar

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import de.oljg.glac.core.navigation.common.GlacScreen
import de.oljg.glac.core.navigation.ui.components.NavigationBarItemIcon
import de.oljg.glac.core.navigation.ui.components.NavigationBarItemLabel
import de.oljg.glac.core.navigation.ui.utils.NavigationDefaults.navItemLabels

@Composable
fun GlacBottomNavigationBar(
    bottomNavigationBarScreens: List<GlacScreen>,
    selected: (GlacScreen) -> Boolean,
    onNavigationBarItemIsClicked: (GlacScreen) -> Unit
) {
    NavigationBar {
        bottomNavigationBarScreens.forEach { screen ->
            NavigationBarItem(
                selected = selected(screen),
                onClick = { onNavigationBarItemIsClicked(screen) },
                label = { NavigationBarItemLabel(screen, navItemLabels) },
                icon = { NavigationBarItemIcon(screen, navItemLabels, selected(screen)) }
            )
        }
    }
}

