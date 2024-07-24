package de.oljg.glac.feature_clock.ui.settings


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import de.oljg.glac.core.utils.ScreenDetails
import de.oljg.glac.core.utils.TestTags.CLOCK_SETTINGS_SCREEN
import de.oljg.glac.core.utils.screenDetails
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.settings.components.sections.ClockBrightnessSettings
import de.oljg.glac.feature_clock.ui.settings.components.sections.ClockCharacterSettings
import de.oljg.glac.feature_clock.ui.settings.components.sections.ClockColorSettings
import de.oljg.glac.feature_clock.ui.settings.components.sections.ClockDisplaySettings
import de.oljg.glac.feature_clock.ui.settings.components.sections.ClockDividerSettings
import de.oljg.glac.feature_clock.ui.settings.components.sections.ClockPreview
import de.oljg.glac.feature_clock.ui.settings.components.sections.ClockThemeSettings
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DEFAULT_DEBOUNCE_TIMEOUT
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DEFAULT_HORIZONTAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.SETTINGS_SCREEN_HORIZONTAL_OUTER_PADDING
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.SETTINGS_SCREEN_PREVIEW_SPACE
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@Composable
fun ClockSettingsScreen(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val screenDetails = screenDetails()
    val screenWidthType = screenDetails.screenWidthType
    val screenHeightType = screenDetails.screenHeightType

    fun mustBeOneColumn() = screenWidthType is ScreenDetails.DisplayType.Compact
            // E.g. small phones are Medium, but two columns are too much, content is too big
            || (screenWidthType is ScreenDetails.DisplayType.Medium
            && screenHeightType is ScreenDetails.DisplayType.Compact)

    Surface(
        modifier = Modifier.fillMaxSize().testTag(CLOCK_SETTINGS_SCREEN),
        color = MaterialTheme.colorScheme.background
    ) {
        Column( // fixed outer column
            modifier = Modifier.padding(horizontal = SETTINGS_SCREEN_HORIZONTAL_OUTER_PADDING),
            verticalArrangement = Arrangement.Top
        ) {
            ClockPreview(clockSettings, onEvent)
            Spacer(modifier = Modifier.height(SETTINGS_SCREEN_PREVIEW_SPACE))
            when {
                mustBeOneColumn() -> OneColumnLayout(clockSettings, onEvent)
                else -> TwoColumnsLayout(clockSettings, onEvent)
            }
            Spacer(modifier = Modifier.height(ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE / 2))
        }
    }
}


@OptIn(FlowPreview::class)
@Composable
private fun OneColumnLayout(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val scrollState = rememberScrollState(initial = clockSettings.clockSettingsColumnScrollPosition)

    /**
     * Persist column scroll position DEFAULT_DEBOUNCE_TIMEOUT millis after the column is scrolled.
     * This way, users can close the app (or even reboot device), and then continue, where they
     * left off.
     *
     * Source/Idea:
     * https://github.com/philipplackner/PersistentScrollPositionCompose/tree/master
     */
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .debounce(DEFAULT_DEBOUNCE_TIMEOUT)
            .collectLatest { scrollValue ->
                onEvent(ClockSettingsEvent.UpdateClockSettingsColumnScrollPosition(scrollValue))
            }
    }

    Column( // inner scrollable column
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        ClockThemeSettings(clockSettings, onEvent)
        ClockDisplaySettings(clockSettings, onEvent)
        ClockCharacterSettings(clockSettings, onEvent)
        ClockDividerSettings(clockSettings, onEvent)
        ClockColorSettings(clockSettings, onEvent)
        ClockBrightnessSettings(clockSettings, onEvent)
    }
}


@OptIn(FlowPreview::class)
@Composable
private fun TwoColumnsLayout(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val scrollStateStartColumn = rememberScrollState(
        initial = clockSettings.clockSettingsStartColumnScrollPosition
    )
    val scrollStateEndColumn = rememberScrollState(
        initial = clockSettings.clockSettingsEndColumnScrollPosition
    )

    LaunchedEffect(scrollStateStartColumn) {
        snapshotFlow { scrollStateStartColumn.value }
            .debounce(DEFAULT_DEBOUNCE_TIMEOUT)
            .collectLatest { scrollValue ->
                onEvent(
                    ClockSettingsEvent
                        .UpdateClockSettingsStartColumnScrollPosition(scrollValue)
                )
            }
    }

    LaunchedEffect(scrollStateEndColumn) {
        snapshotFlow { scrollStateEndColumn.value }
            .debounce(DEFAULT_DEBOUNCE_TIMEOUT)
            .collectLatest { scrollValue ->
                onEvent(ClockSettingsEvent.UpdateClockSettingsEndColumnScrollPosition(scrollValue))
            }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column( // inner left/start scrollable column
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollStateStartColumn)
        ) {
            ClockThemeSettings(clockSettings, onEvent)
            ClockDisplaySettings(clockSettings, onEvent)
            ClockCharacterSettings(clockSettings, onEvent)
        }
        Spacer(
            modifier = Modifier
                .width(DEFAULT_HORIZONTAL_SPACE)
                .fillMaxHeight()
        )
        Column( // inner right/end scrollable column
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollStateEndColumn)
        ) {
            ClockDividerSettings(clockSettings, onEvent)
            ClockColorSettings(clockSettings, onEvent)
            ClockBrightnessSettings(clockSettings, onEvent)
        }
    }
}
