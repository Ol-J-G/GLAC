package de.oljg.glac.settings.clock.ui


import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.core.util.ScreenDetails
import de.oljg.glac.core.util.screenDetails
import de.oljg.glac.settings.clock.ui.components.sections.ClockBrightnessSettings
import de.oljg.glac.settings.clock.ui.components.sections.ClockCharacterSettings
import de.oljg.glac.settings.clock.ui.components.sections.ClockColorSettings
import de.oljg.glac.settings.clock.ui.components.sections.ClockDisplaySettings
import de.oljg.glac.settings.clock.ui.components.sections.ClockDividerSettings
import de.oljg.glac.settings.clock.ui.components.sections.ClockPreview
import de.oljg.glac.settings.clock.ui.components.sections.ClockThemeSettings
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_DEBOUNCE_TIMEOUT
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_HORIZONTAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_SCREEN_HORIZONTAL_OUTER_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_SCREEN_PREVIEW_SPACE
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockSettingsScreen() {
    val screenDetails = screenDetails()
    val screenWidthType = screenDetails.screenWidthType
    val screenHeightType = screenDetails.screenHeightType

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column( // fixed outer column
            modifier = Modifier.padding(horizontal = SETTINGS_SCREEN_HORIZONTAL_OUTER_PADDING),
            verticalArrangement = Arrangement.Top
        ) {
            ClockPreview()
            Spacer(modifier = Modifier.height(SETTINGS_SCREEN_PREVIEW_SPACE))
            when {
                screenWidthType is ScreenDetails.DisplayType.Compact -> OneColumnLayout()

                // E.g. small phones are Medium, but two columns are too much, content is too big
                screenWidthType is ScreenDetails.DisplayType.Medium
                        && screenHeightType is ScreenDetails.DisplayType.Compact ->
                    OneColumnLayout()

                else -> TwoColumnsLayout()
            }
            Spacer(modifier = Modifier.height(SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2))
        }
    }
}


@OptIn(FlowPreview::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun OneColumnLayout(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val scrollState = rememberScrollState(initial = clockSettings.columnScrollPosition)

    /**
     * Persist column scroll position DEFAULT_DEBOUNCE_TIMEOUT millis after the column is scrolled.
     * This way, users can close the app (or even reboot device), and then continue, where they
     * left off.
     *
     * Soucre/Idea:
     * https://github.com/philipplackner/PersistentScrollPositionCompose/tree/master
     */
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .debounce(DEFAULT_DEBOUNCE_TIMEOUT)
            .collectLatest { scrollValue ->
                viewModel.updateColumnScrollPosition(clockSettings, scrollValue)
            }
    }

    Column( // inner scrollable column
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        ClockThemeSettings()
        ClockDisplaySettings()
        ClockCharacterSettings()
        ClockDividerSettings()
        ClockColorSettings()
        ClockBrightnessSettings()
    }
}


@OptIn(FlowPreview::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TwoColumnsLayout(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val scrollStateStartColumn = rememberScrollState(
        initial = clockSettings.startColumnScrollPosition
    )
    val scrollStateEndColumn = rememberScrollState(initial = clockSettings.endColumnScrollPosition)

    LaunchedEffect(scrollStateStartColumn) {
        snapshotFlow { scrollStateStartColumn.value }
            .debounce(DEFAULT_DEBOUNCE_TIMEOUT)
            .collectLatest { scrollValue ->
                viewModel.updateStartColumnScrollPosition(clockSettings, scrollValue)
            }
    }

    LaunchedEffect(scrollStateEndColumn) {
        snapshotFlow { scrollStateEndColumn.value }
            .debounce(DEFAULT_DEBOUNCE_TIMEOUT)
            .collectLatest { scrollValue ->
                viewModel.updateEndColumnScrollPosition(clockSettings, scrollValue)
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
            ClockThemeSettings()
            ClockDisplaySettings()
            ClockCharacterSettings()
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
            ClockDividerSettings()
            ClockColorSettings()
            ClockBrightnessSettings()
        }
    }
}
