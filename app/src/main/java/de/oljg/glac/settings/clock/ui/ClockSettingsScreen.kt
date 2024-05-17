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
import androidx.compose.ui.Modifier
import de.oljg.glac.clock.digital.ui.utils.ScreenDetails
import de.oljg.glac.clock.digital.ui.utils.evaluateScreenDetails
import de.oljg.glac.settings.clock.ui.components.color.ColorSelector
import de.oljg.glac.settings.clock.ui.components.sections.ClockCharacterSettings
import de.oljg.glac.settings.clock.ui.components.sections.ClockColorSettings
import de.oljg.glac.settings.clock.ui.components.sections.ClockDisplaySettings
import de.oljg.glac.settings.clock.ui.components.sections.ClockDividerSettings
import de.oljg.glac.settings.clock.ui.components.sections.ClockPreview
import de.oljg.glac.settings.clock.ui.components.sections.ClockThemeSettings
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_HORIZONTAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_SCREEN_HORIZONTAL_OUTER_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_SCREEN_PREVIEW_SPACE

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockSettingsScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column( // fixed outer column
            modifier = Modifier.padding(horizontal = SETTINGS_SCREEN_HORIZONTAL_OUTER_PADDING),
            verticalArrangement = Arrangement.Top
        ) {
            ClockPreview()
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(SETTINGS_SCREEN_PREVIEW_SPACE)
            )

            /**
             * One column layout is better with medium screens, especially because
             * navigation rail will be on left/start edge, and then each column af two columns
             * layout is too small (e.g. [ColorSelector]'s color swatches get squeezesd^^..OO)
             */
            when (evaluateScreenDetails().screenWidthType) {
                is ScreenDetails.DisplayType.Expanded -> TwoColumnsLayout()
                else -> OneColumnLayout()
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TwoColumnsLayout() {
    val scrollStateStartColumn = rememberScrollState()
    val scrollStateEndColumn = rememberScrollState()
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
        Spacer(modifier = Modifier.width(DEFAULT_HORIZONTAL_SPACE).fillMaxHeight())
        Column( // inner right/end scrollable column
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollStateEndColumn)
        ) {
            ClockDividerSettings()
            ClockColorSettings()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun OneColumnLayout() {
    val scrollState = rememberScrollState()
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
    }
}
