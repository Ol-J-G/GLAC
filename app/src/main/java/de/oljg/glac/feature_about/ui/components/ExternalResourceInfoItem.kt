package de.oljg.glac.feature_about.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.BulletPointRow
import de.oljg.glac.core.util.CommonUtils.SPACE
import de.oljg.glac.feature_about.data.ExternalResourceInfo
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.INFO_ITEM_CARD_CORNER_SIZE
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.INFO_ITEM_DIVIDER_PADDING
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.INFO_ITEM_LICENCE_TAG
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.INFO_ITEM_MOD_BULLET_POINT_CIRCLE_SIZE
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.INFO_ITEM_MOD_ELEMENTS_SPACE
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.INFO_ITEM_PADDING
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.INFO_ITEM_SOURCE_TAG
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.INFO_ITEM_SPACE

@Composable
fun ExternalResourceInfoItem(
    externalResourceInfo: ExternalResourceInfo,
    renamedTo: String? = null,
    modified: String? = null,
    modifications: List<String>? = null
) {
    Card(shape = RoundedCornerShape(percent = INFO_ITEM_CARD_CORNER_SIZE)) {
        Column(
            modifier = Modifier
                .padding(INFO_ITEM_PADDING)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(INFO_ITEM_SPACE)
        ) {
            Text(
                text = externalResourceInfo.title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(stringResource(R.string.by))
            externalResourceInfo.authors.forEach { author ->
                Text(
                    text = author,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            }

            // "Source / Licence" web links
            WebLinkText(
                annotatedString = buildAnnotatedString {
                    pushStringAnnotation(
                        tag = INFO_ITEM_SOURCE_TAG,
                        annotation = externalResourceInfo.sourceUriString
                    )
                    withStyle(style = AboutScreenDefaults.hyperlink) {
                        append(externalResourceInfo.sourceName)
                    }
                    pop()
                    append(SPACE)
                    append('/')
                    append(SPACE)
                    pushStringAnnotation(
                        tag = INFO_ITEM_LICENCE_TAG,
                        annotation = externalResourceInfo.licence.url
                    )
                    withStyle(style = AboutScreenDefaults.hyperlink) {
                        append(externalResourceInfo.licence.name)
                    }
                    pop()
                },
                tags = listOf(INFO_ITEM_SOURCE_TAG, INFO_ITEM_LICENCE_TAG)
            )

            if (renamedTo != null || modified != null || modifications != null) {
                Divider(
                    modifier = Modifier.padding(vertical = INFO_ITEM_DIVIDER_PADDING),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            renamedTo?.let {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.renamed_to))
                            append(SPACE)
                            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                append(it)
                            }
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }

            modified?.let {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.modified_by))
                            append(SPACE)
                            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                append(it)
                            }
                        }
                    )
                }
            }

            modifications?.let {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(INFO_ITEM_MOD_ELEMENTS_SPACE)
                ) {
                    it.forEachIndexed { index, modification ->
                        BulletPointRow(
                            number = "${index + 1}",
                            text = modification,
                            circleSize = INFO_ITEM_MOD_BULLET_POINT_CIRCLE_SIZE,
                            circleColor = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
        }
    }
}
