package de.oljg.glac.feature_clock.ui.clock.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit

@Composable
fun ClockCharColumn(
    char: Char,
    columnWidth: Dp,
    columnHeight: Dp,
    charSize: DpSize,
    fontSize: TextUnit,
    charColor: Color,
    testTag: String,
    clockChar: @Composable (Char, TextUnit, Color, DpSize) -> Unit
    ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .requiredSize( // important to enforce monospace!
                columnWidth,
                columnHeight
            )
            .testTag(testTag)
    ) {
        clockChar(
            char,
            fontSize,
            charColor,
            charSize,
        )
    }
}

