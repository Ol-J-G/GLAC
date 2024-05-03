package de.oljg.glac.settings.clock.ui


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.oljg.glac.settings.clock.ui.components.sections.ClockPreview
import de.oljg.glac.settings.clock.ui.components.sections.ClockCharacterSettings
import de.oljg.glac.settings.clock.ui.components.sections.ClockDisplaySettings
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_SCREEN_HORIZONTAL_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_SCREEN_VERTICAL_PADDING


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockSettingsScreen() {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column( // fixed outer column
            modifier = Modifier
                .padding(
                    horizontal = SETTINGS_SCREEN_HORIZONTAL_PADDING,
                    vertical = SETTINGS_SCREEN_VERTICAL_PADDING
                ),
            verticalArrangement = Arrangement.Top
        ) {
            ClockPreview()

            Row(modifier = Modifier.fillMaxWidth()) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            Column( // inner scrollable column, actual settings
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                ClockDisplaySettings()
                ClockCharacterSettings()
            }
        }
    }
}
