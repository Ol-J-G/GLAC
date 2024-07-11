package de.oljg.glac.feature_about.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString


/**
 * Inspired by:
 * https://stackoverflow.com/questions/65567412/jetpack-compose-text-hyperlink-some-section-of-the-text
 */
@Composable
fun WebLinkText(
    annotatedString: AnnotatedString,
    tags: List<String>
) {
    val uriHandler = LocalUriHandler.current

    ClickableText(
        text = annotatedString,
        style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
        onClick = { offset ->
            tags.forEach { tag ->
                annotatedString.getStringAnnotations(
                    tag = tag,
                    start = offset,
                    end = offset
                ).firstOrNull()?.let { range -> uriHandler.openUri(range.item) }
            }
        }
    )
}
