package de.oljg.glac.alarms.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.isInt
import de.oljg.glac.alarms.ui.utils.isIntIn
import de.oljg.glac.alarms.ui.utils.translateDuration
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun DurationSelector(
    modifier: Modifier = Modifier,
    label: String,
    duration: Duration,
    durationUnit: DurationUnit,
    minDuration: Duration,
    maxDuration: Duration,
    onDurationChanged: (Duration) -> Unit,
    onValueChanged: (Boolean) -> Unit = {}
) {
    var durationValue by remember {
        mutableStateOf(duration.toInt(unit = durationUnit).toString())
    }
    var isValidDuration by remember {
        mutableStateOf(true)
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = modifier,
            value = durationValue,
            label = { Text(text = label + " [" + translateDuration(unit = durationUnit) + "]") },
            onValueChange = { newTextValue ->
                durationValue = newTextValue.trim()
                isValidDuration = durationValue.isIntIn(
                    range = minDuration.toInt(durationUnit)..maxDuration.toInt(durationUnit)
                )
                onValueChanged(isValidDuration)

                if (isValidDuration)
                    onDurationChanged(durationValue.toInt().toDuration(durationUnit))
            },
            singleLine = true,
            supportingText = {
                AnimatedVisibility(visible = !isValidDuration) {
                    Text(
                        text = when {
                            durationValue.isBlank() -> stringResource(R.string.please_enter_a_number)
                            !durationValue.isInt() -> stringResource(R.string.please_enter_a_whole_number)
                            durationValue.toInt() !in
                                    minDuration.toInt(durationUnit)
                                    ..maxDuration.toInt(durationUnit) ->
                                stringResource(R.string.valid_range) + ": $minDuration - $maxDuration"

                            else -> ""
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                AnimatedVisibility(visible = !isValidDuration) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = stringResource(R.string.invalid) + " $label",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )
    }
}
