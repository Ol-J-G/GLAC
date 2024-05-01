package de.oljg.glac.settings.clock.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import de.oljg.glac.clock.digital.ui.utils.ClockCharType

@Composable
fun ClockCharTypeSelector(
    label: String,
    selectedClockCharType: String,
    onClockCharTypeSelected: (String) -> Unit
) {
    val radioOptions = listOf(ClockCharType.FONT.name, ClockCharType.SEVEN_SEGMENT.name)

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Text(label)
        Row(Modifier.selectableGroup()) {
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .height(56.dp)
                        .selectable(
                            selected = (text == selectedClockCharType),
                            onClick = {
                                onClockCharTypeSelected(text)
                            },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedClockCharType),
                        onClick = null // null recommended for accessibility with screenreaders
                    )
                    Text(
                        text = if(text == ClockCharType.FONT.name) "Font" else "7-Segment", //TODO: add res, etc
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}
