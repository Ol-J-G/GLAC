package de.oljg.glac.settings.clock.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import de.oljg.glac.clock.digital.ui.DigitalClockScreen
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.evaluateScreenDetails
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.PREVIEW_SIZE_FACTOR

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsSection(
    sectionTitle: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    settingsContent: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            1.dp, if (expanded)
                MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        if (expanded) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                    .clickable(onClick = { onExpandedChange(!expanded) }),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp),
                    text = sectionTitle,
                    style = MaterialTheme.typography.titleLarge
                )
                if (expanded)
                    Icon(
                        modifier = Modifier
                            .padding(end = 12.dp),
                        imageVector = Icons.Filled.KeyboardDoubleArrowUp,
                        contentDescription = null
                    )
                else
                    Icon(
                        modifier = Modifier
                            .padding(end = 12.dp),
                        imageVector = Icons.Filled.KeyboardDoubleArrowDown,
                        contentDescription = null
                    )
            }

            val density = LocalDensity.current
            AnimatedVisibility(
                visible = expanded,
                enter = slideInVertically {
                    with(density) { -40.dp.roundToPx() }
                } + expandVertically(
                    // Expand from the top.
                    expandFrom = Alignment.Top
                ) + fadeIn(
                    initialAlpha = 0.1f
                ),
                exit = slideOutVertically() + shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    settingsContent.invoke()

                    Divider(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .requiredSize(
                                DpSize(
                                    evaluateScreenDetails().screenWidth * PREVIEW_SIZE_FACTOR,
                                    evaluateScreenDetails().screenHeight * PREVIEW_SIZE_FACTOR
                                )
                            )
                            .align(Alignment.CenterHorizontally)
                    ) {
                          DigitalClockScreen(previewMode = true, clockCharType = ClockCharType.FONT)
                    }
                }
            }
        }
    }
}

