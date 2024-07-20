package de.oljg.glac.feature_about.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.BulletPointRow
import de.oljg.glac.core.util.CommonUtils.SPACE
import de.oljg.glac.core.util.FontWeight
import de.oljg.glac.feature_about.data.ExternalResourceInfo
import de.oljg.glac.feature_about.data.SoundMods
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
    modifications: List<SoundMods>? = null,
    weights: List<FontWeight>? = null,
    includesItalic: Boolean? = null
) {
    Card(shape = RoundedCornerShape(INFO_ITEM_CARD_CORNER_SIZE)) {
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

            // In case of font => add weights etc. here as some kind of sub title info ...
            weights?.let {
                Text(
                    text = buildAnnotatedString {
                        weights.forEachIndexed { index, weight ->
                            when (weight) {
                                FontWeight.THIN -> append(stringResource(R.string.thin))
                                FontWeight.EXTRA_LIGHT -> {
                                    append(stringResource(R.string.extra) +
                                            SPACE + stringResource(R.string.light))
                                }
                                FontWeight.LIGHT -> append(stringResource(R.string.light))
                                FontWeight.NORMAL -> append(stringResource(R.string.normal))
                                FontWeight.MEDIUM -> append(stringResource(R.string.medium))
                                FontWeight.SEMI_BOLD -> {
                                    append(stringResource(R.string.semi) +
                                            SPACE + stringResource(R.string.bold))
                                }
                                FontWeight.BOLD -> append(stringResource(R.string.bold))
                                FontWeight.EXTRA_BOLD -> {
                                    append(stringResource(R.string.extra) +
                                            SPACE + stringResource(R.string.bold))
                                }
                                FontWeight.BLACK -> append(stringResource(R.string.black))
                            }
                            if (index + 1 < weights.size) { // no separator after last weight
                                append(',')
                                append(SPACE)
                            }
                        }
                        includesItalic?.let {
                            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                append(SPACE)
                                append('(')
                                append(stringResource(R.string.italic_style_is_available))
                                append(')')
                            }
                        }
                    },
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }

            Text(stringResource(R.string.by))
            externalResourceInfo.authors.forEach { author ->
                Text(
                    text = author,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(INFO_ITEM_DIVIDER_PADDING))

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
                    append('\u2E31') // "Word Separator Middle Dot"
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
                HorizontalDivider(
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
                            text = when(modification) {
                                SoundMods.NORMALIZATION -> stringResource(R.string.normalized)
                                SoundMods.NOISE_REDUCTION -> stringResource(R.string.noise_reduced)
                                SoundMods.CUTTED -> stringResource(R.string.cutted)
                                SoundMods.CONVERTED_TO_OGG ->
                                    stringResource(R.string.converted_to_ogg)
                            },
                            circleSize = INFO_ITEM_MOD_BULLET_POINT_CIRCLE_SIZE,
                            circleColor = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
        }
    }
}
