package de.oljg.glac.core.navigation.ui.topappbar.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.Dp
import de.oljg.glac.core.navigation.ui.topappbar.util.Constants.INACTIVE_TAB_OPACITY
import de.oljg.glac.core.navigation.ui.topappbar.util.Constants.TAB_FADE_IN_ANIMATION_DELAY
import de.oljg.glac.core.navigation.ui.topappbar.util.Constants.TAB_FADE_IN_ANIMATION_DURATION
import de.oljg.glac.core.navigation.ui.topappbar.util.Constants.TAB_FADE_OUT_ANIMATION_DURATION
import de.oljg.glac.core.navigation.ui.topappbar.util.Constants.TAB_PADDING
import de.oljg.glac.core.navigation.ui.topappbar.util.Constants.TAB_TEXT_ICON_SPACE
import de.oljg.glac.core.navigation.ui.topappbar.util.Constants.TOP_APP_BAR_HEIGHT
import de.oljg.glac.core.util.defaultColor
import java.util.Locale

@Composable
fun GlacTab(
    tabText: String,
    tabIconFilled: ImageVector,
    tabIconOutlined: ImageVector,
    onSelected: () -> Unit,
    tabIsSelected: Boolean,
    testTag: String
) {
    val color = defaultColor()
    val durationMillis = if (tabIsSelected) TAB_FADE_IN_ANIMATION_DURATION else TAB_FADE_OUT_ANIMATION_DURATION
    val animSpec = remember {
        tween<Color>(
            durationMillis = durationMillis,
            easing = LinearEasing,
            delayMillis = TAB_FADE_IN_ANIMATION_DELAY
        )
    }
    val tabTintColor by animateColorAsState(
        targetValue = if (tabIsSelected) color else color.copy(alpha = INACTIVE_TAB_OPACITY),
        animationSpec = animSpec,
        label = "Tab tint color animation"
    )
    Row(
        modifier = Modifier
            .padding(TAB_PADDING)
            .animateContentSize()
            .height(TOP_APP_BAR_HEIGHT)
            .selectable(
                selected = tabIsSelected,
                onClick = onSelected,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = Dp.Unspecified,
                    color = Color.Unspecified
                )
            )
            .clearAndSetSemantics {
                contentDescription = tabText
                this.testTag = testTag
            }
    ) {
        if (tabIsSelected) {
            Icon(
                imageVector = tabIconFilled,
                contentDescription = tabText,
                tint = tabTintColor
            )
            Spacer(modifier = Modifier.width(TAB_TEXT_ICON_SPACE))
            Text(
                text = tabText.uppercase(Locale.getDefault()),
                color = tabTintColor
            )
        } else {
            Icon(
                imageVector = tabIconOutlined,
                contentDescription = tabText,
                tint = tabTintColor
            )
        }
    }
}

