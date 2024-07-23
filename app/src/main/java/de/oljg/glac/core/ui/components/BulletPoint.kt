package de.oljg.glac.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import de.oljg.glac.core.util.CoreLayoutDefaults.BULLET_POINT_ELEMENTS_HORIZONTAL_SPACE
import de.oljg.glac.core.util.CoreLayoutDefaults.BULLET_POINT_NUMBER_SHAPE_SIZE
import de.oljg.glac.core.util.CoreLayoutDefaults.BULLET_POINT_TEXT_LINE_HEIGHT


@Composable
fun BulletPointRow(
    number: String,
    text: String,
    circleSize: Dp = BULLET_POINT_NUMBER_SHAPE_SIZE,
    circleColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            BULLET_POINT_ELEMENTS_HORIZONTAL_SPACE,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BulletPointNumber(number, circleSize, circleColor)
        Text(
            text = text,
            textAlign = TextAlign.Justify,
            lineHeight = BULLET_POINT_TEXT_LINE_HEIGHT
        )
    }
}


@Composable
private fun BulletPointNumber(
    number: String,
    circleSize: Dp = BULLET_POINT_NUMBER_SHAPE_SIZE,
    circleColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .size(circleSize)
            .background(circleColor)
    ) {
        Text(
            text = number,
            fontFamily = FontFamily.Monospace
        )
    }
}
