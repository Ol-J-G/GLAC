package de.oljg.glac.feature_about.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import de.oljg.glac.core.util.ScreenDetails
import de.oljg.glac.core.util.screenDetails
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
    val screenDetails = screenDetails()
    val screenWidthType = screenDetails.screenWidthType
    val screenHeightType = screenDetails.screenHeightType

    fun mustBeOneColumn() = screenWidthType is ScreenDetails.DisplayType.Compact
            // E.g. small phones are Medium, but two columns are too much, content is too big
            || (screenWidthType is ScreenDetails.DisplayType.Medium
            && screenHeightType is ScreenDetails.DisplayType.Compact)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(OUTER_SURFACE_PADDING),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            mustBeOneColumn() -> OneColumnLayout()
            else -> TwoColumnsLayout()
        }
    }
}


@Composable
private fun OneColumnLayout() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(OUTER_COLUMN_ELEMENTS_SPACE)
    ) {
        AboutInfo()
        Spacer(modifier = Modifier.height(SPACER_HEIGHT))
        ExternalSoundResources()
        Spacer(modifier = Modifier.height(SPACER_HEIGHT))
        ExternalFontResources()
    }
}


@Composable
private fun TwoColumnsLayout() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(OUTER_COLUMN_ELEMENTS_SPACE)
    ) {
        AboutInfo()
        Spacer(modifier = Modifier.height(SPACER_HEIGHT))

        Row(horizontalArrangement = Arrangement.spacedBy(OUTER_COLUMN_ELEMENTS_SPACE)) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(OUTER_COLUMN_ELEMENTS_SPACE),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExternalSoundResources()
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(OUTER_COLUMN_ELEMENTS_SPACE),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExternalFontResources()
            }
        }
    }
}


@Composable
private fun AboutInfo() {
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
}


@Composable
private fun ExternalSoundResources() {
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
}


@Composable
private fun ExternalFontResources() {
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
