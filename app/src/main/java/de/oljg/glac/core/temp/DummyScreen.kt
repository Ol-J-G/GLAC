package de.oljg.glac.core.temp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun DummyScreen(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(color)
        .clickable(enabled = true, onClick = onClick)
    ) {
        Text(
            modifier = modifier.align(Alignment.Center),
            text = text
        )
    }
}