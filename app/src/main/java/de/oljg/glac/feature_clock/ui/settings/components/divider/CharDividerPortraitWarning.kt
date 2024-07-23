package de.oljg.glac.feature_clock.ui.settings.components.divider

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults

@Composable
fun CharDividerPortraitWarning() {
    Surface(
        modifier = Modifier
            .padding(vertical = ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE),
        shape = RoundedCornerShape(ClockSettingsDefaults.DEFAULT_ROUNDED_CORNER_SIZE),
        border = BorderStroke(
            width = ClockSettingsDefaults.DEFAULT_BORDER_WIDTH,
            color = MaterialTheme.colorScheme.error
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE),
            verticalArrangement = Arrangement.spacedBy(
                ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE * 2,
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.caution),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = stringResource(
                    R.string.character_dividers_are_not_really_useful_in_portrait_mode),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                stringResource(
                    R.string.line_dividers_have_been_selected_automatically_as_a_replacement),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                stringResource(
                    R.string.please_select_a_different_divider_style_to_make_further_settings),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                stringResource(
                    R.string.alternatively_rotate_your_device_to_set_up_character_dividers),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
