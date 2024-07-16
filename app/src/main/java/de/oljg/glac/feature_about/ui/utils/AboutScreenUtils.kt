package de.oljg.glac.feature_about.ui.utils

import android.util.Patterns
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp


fun String.isWebUrl() = Patterns.WEB_URL.matcher(this).matches()


object AboutScreenDefaults {
    val D_DIN: FontFamily @Composable get() =
        FontFamily(Font(path = "fonts/D_Din_Regular.ttf", LocalContext.current.assets))

    val OUTER_SURFACE_PADDING = 24.dp
    val OUTER_COLUMN_ELEMENTS_SPACE = 20.dp
    val SPACER_HEIGHT = 32.dp

    const val INFO_ITEM_CARD_CORNER_SIZE = 10
    val INFO_ITEM_PADDING = 24.dp
    val INFO_ITEM_SPACE = 12.dp
    val INFO_ITEM_DIVIDER_PADDING = 8.dp
    val INFO_ITEM_MOD_ELEMENTS_SPACE = 8.dp
    val INFO_ITEM_MOD_BULLET_POINT_CIRCLE_SIZE = 24.dp

    val hyperlink: SpanStyle @Composable get() =
        SpanStyle(color = MaterialTheme.colorScheme.primary)

    const val AUTHOR_NAME = "Oliver Götze"

    const val GLAC_GITHUB_TAG = "projectlink"
    const val GLAC_GITHUB_URL = "https://github.com/Ol-J-G/GLAC"

    const val INFO_ITEM_SOURCE_TAG = "source"
    const val INFO_ITEM_LICENCE_TAG = "licence"
}
