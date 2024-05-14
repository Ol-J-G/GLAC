package de.oljg.glac.settings.clock.ui.components.divider

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.CHAR_SELECTOR_TF_TOP_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.CHAR_SELECTOR_TF_WIDTH
import de.oljg.glac.settings.clock.ui.utils.isValidDividerChar

@Composable
fun DividerCharSelector(
    title: String,
    char: Char,
    onCharChanged: (Char) -> Unit
) {
    var textFieldInput by remember {
        mutableStateOf(char.toString())
    }
    var isValidInput by remember {
        mutableStateOf(true)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)
        OutlinedTextField(
            modifier = Modifier
                .width(CHAR_SELECTOR_TF_WIDTH)
                .padding(top = CHAR_SELECTOR_TF_TOP_PADDING),
            label = { Text(stringResource(R.string.character)) },
            value = textFieldInput,
            onValueChange = { newValue ->
                textFieldInput = newValue
                isValidInput = textFieldInput.isValidDividerChar()
                if(isValidInput) onCharChanged(textFieldInput.toCharArray()[0])
            },
            supportingText = {
                if (!isValidInput)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.invalid_char) + "!",
                        color = MaterialTheme.colorScheme.error
                    )
            },
            singleLine = true,
        )
    }
}
