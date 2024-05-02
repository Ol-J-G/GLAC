package de.oljg.glac.settings.clock.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import de.oljg.glac.clock.digital.ui.DigitalClockScreen
import de.oljg.glac.clock.digital.ui.utils.evaluateScreenDetails
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockPreview() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(
                    DpSize(
                        evaluateScreenDetails().screenWidth * SettingsDefaults.PREVIEW_SIZE_FACTOR,
                        evaluateScreenDetails().screenHeight * SettingsDefaults.PREVIEW_SIZE_FACTOR
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            DigitalClockScreen(previewMode = true)
        }
    }
}
