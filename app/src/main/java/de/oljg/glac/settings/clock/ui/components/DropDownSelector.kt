package de.oljg.glac.settings.clock.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownSelector(
    label: String,
    selectedValue: String,
    onNewValueSelected: (String) -> Unit,
    values: List<String>,
    prettyPrintValue: (String) -> String = { value -> value },
    maxWidthFraction: Float = .7f,
    addValueComponent: @Composable () -> Unit = {}
) {
    var dropDownIsExpanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingsDefaults.DROPDOWN_ROW_VERTICAL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(end = SettingsDefaults.DROPDOWN_END_PADDING),
            text = label,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            addValueComponent.invoke()
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(end = SettingsDefaults.DROPDOWN_END_PADDING),
                expanded = dropDownIsExpanded,
                onExpandedChange = { dropDownIsExpanded = !dropDownIsExpanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(maxWidthFraction)
                        .clickable(onClick = { dropDownIsExpanded = true }),
                    value = prettyPrintValue(selectedValue),
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium,
                    trailingIcon = { TrailingIcon(expanded = dropDownIsExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = dropDownIsExpanded,
                    onDismissRequest = { dropDownIsExpanded = false }
                ) {
                    values.forEach { value ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = prettyPrintValue(value),
                                    fontSize = 18.sp
                                )
                            },
                            onClick = {
                                onNewValueSelected(value)
                                dropDownIsExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
