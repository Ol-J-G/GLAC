package de.oljg.glac.feature_about.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import de.oljg.glac.R
import de.oljg.glac.core.util.CommonUtils.SPACE
import de.oljg.glac.feature_about.data.externalFonts
import de.oljg.glac.feature_about.data.externalSounds
import de.oljg.glac.feature_about.ui.components.ExternalResourceInfoItem
import de.oljg.glac.feature_about.ui.components.WebLinkText
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.AUTHOR_NAME
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.D_DIN
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.GLAC_GITHUB_TAG
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.GLAC_GITHUB_URL
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.OUTER_COLUMN_ELEMENTS_SPACE
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.OUTER_SURFACE_PADDING
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.SPACER_HEIGHT
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.hyperlink

@Composable
fun AboutScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(OUTER_SURFACE_PADDING),
        color = MaterialTheme.colorScheme.background
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(OUTER_COLUMN_ELEMENTS_SPACE)
        ) {
            Text(
                text = stringResource(R.string.gentle_light_alarm_clock),
                style = MaterialTheme.typography.headlineLarge.copy(fontFamily = D_DIN)
            )
            Text(
                text = stringResource(R.string.by),
                style = LocalTextStyle.current.copy(fontFamily = D_DIN)
            )
            Text(
                text = AUTHOR_NAME,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Cursive // It's "Dancing Script" currently
                )
            )
            WebLinkText(
                annotatedString = buildAnnotatedString {
                    append(stringResource(R.string.visit))
                    append(SPACE)
                    pushStringAnnotation(
                        tag = GLAC_GITHUB_TAG,
                        annotation = GLAC_GITHUB_URL
                    )
                    withStyle(style = hyperlink) {
                        append(stringResource(R.string.glac_on_github))
                    }
                    pop()
                },
                tags = listOf(GLAC_GITHUB_TAG)
            )

            Spacer(modifier = Modifier.height(SPACER_HEIGHT))
            Text(
                text = stringResource(R.string.external_sound_resources),
                style = MaterialTheme.typography.headlineSmall
            )
            externalSounds
                .sortedBy { it.title.lowercase() }
                .forEach { externalResourceInfo ->
                ExternalResourceInfoItem(
                    externalResourceInfo = externalResourceInfo,
                    renamedTo = externalResourceInfo.renamedTo,
                    modified = externalResourceInfo.modifiedBy,
                    modifications = externalResourceInfo.modifications
                )
            }

            Spacer(modifier = Modifier.height(SPACER_HEIGHT))
            Text(
                text = stringResource(R.string.external_font_resources),
                style = MaterialTheme.typography.headlineSmall
            )
            externalFonts
                .sortedBy { it.title.lowercase() }
                .forEach { externalResourceInfo ->
                ExternalResourceInfoItem(
                    externalResourceInfo = externalResourceInfo,
                    weights = externalResourceInfo.weights,
                    includesItalic = externalResourceInfo.includesItalic
                )
            }
        }
    }
}
