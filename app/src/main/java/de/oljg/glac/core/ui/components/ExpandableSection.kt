package de.oljg.glac.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.oljg.glac.core.util.CoreLayoutDefaults.EXPANDABLE_SECTION_BORDER_WIDTH
import de.oljg.glac.core.util.CoreLayoutDefaults.EXPANDABLE_SECTION_HEIGHT
import de.oljg.glac.core.util.CoreLayoutDefaults.EXPANDABLE_SECTION_HORIZONTAL_PADDING
import de.oljg.glac.core.util.CoreLayoutDefaults.EXPANDABLE_SECTION_SURFACE_CORNER_SIZE
import de.oljg.glac.core.util.CoreLayoutDefaults.EXPANDABLE_SECTION_VERTICAL_PADDING
import de.oljg.glac.core.util.CoreLayoutDefaults.TRAILING_ICON_END_PADDING


@Composable
fun ExpandableSection(
    sectionTitle: String,
    sectionTitleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    expanded: Boolean,
    horizontalPadding: Dp = 0.dp,
    verticalPadding: Dp = EXPANDABLE_SECTION_VERTICAL_PADDING,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    expandedBackgroundColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    onExpandedChange: (Boolean) -> Unit,
    testTag: String = "",
    sectionContent: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .testTag(testTag),
        shape = RoundedCornerShape(EXPANDABLE_SECTION_SURFACE_CORNER_SIZE),
        border = BorderStroke(
            width = EXPANDABLE_SECTION_BORDER_WIDTH,
            color = if (expanded)
                MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(EXPANDABLE_SECTION_HEIGHT)
                    .background(if (expanded) expandedBackgroundColor else backgroundColor)
                    .clickable(onClick = { onExpandedChange(!expanded) }),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = EXPANDABLE_SECTION_HORIZONTAL_PADDING),
                    text = sectionTitle,
                    style = sectionTitleStyle
                )
                SettingsSectionTrailingIcon(expanded = expanded)
            }

            val density = LocalDensity.current
            AnimatedVisibility(
                visible = expanded,
                enter = slideInVertically { with(density) { -40.dp.roundToPx() } }
                        + expandVertically(expandFrom = Alignment.Top)
                        + fadeIn(initialAlpha = 0.1f),
                exit = slideOutVertically() + shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = EXPANDABLE_SECTION_HORIZONTAL_PADDING,
                            vertical = verticalPadding
                        )
                ) {
                    sectionContent()
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionTrailingIcon(expanded: Boolean) {
    Icon(
        modifier = Modifier
            .padding(end = TRAILING_ICON_END_PADDING)
            .rotate(if (expanded) 180f else 0f),
        imageVector = Icons.Filled.KeyboardDoubleArrowDown,
        contentDescription = null
    )
}

