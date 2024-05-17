package de.oljg.glac.settings.clock.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_ICON_BUTTON_SIZE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownSelector(
    label: String,
    selectedValue: String,
    onNewValueSelected: (String) -> Unit,
    values: List<String>,
    prettyPrintValue: (String) -> String = { value -> value },
    maxWidthFraction: Float = 1f,
    readOnly: Boolean = true,
    onTextFieldValueChanged: (String) -> Unit = {},
    supportingText: @Composable () -> Unit = {},
    removeValueComponent: @Composable () -> Unit = {},
    addValueComponent: @Composable () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var dropDownIsExpanded by remember {
        mutableStateOf(false)
    }

    var textFieldValue by remember(selectedValue) {
        mutableStateOf(selectedValue)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SettingsDefaults.DROPDOWN_ROW_VERTICAL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .padding(end = SettingsDefaults.DROPDOWN_END_PADDING * 3),
                horizontalArrangement = Arrangement.spacedBy(
                    DEFAULT_ICON_BUTTON_SIZE / 8,
                    alignment = Alignment.Start
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                addValueComponent.invoke()
                removeValueComponent.invoke()
            }
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(end = SettingsDefaults.DROPDOWN_END_PADDING),
                expanded = dropDownIsExpanded,
                onExpandedChange = { dropDownIsExpanded = !dropDownIsExpanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(maxWidthFraction),
                    value = prettyPrintValue(selectedValue),
                    label = { Text(label) },
                    onValueChange = { newTextValue ->
                        onTextFieldValueChanged(newTextValue)
                        textFieldValue = newTextValue
                    },
                    readOnly = readOnly,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium,
                    supportingText = supportingText,
                    trailingIcon = {
                        DropDownTrailingIcon(expanded = dropDownIsExpanded) {
                            dropDownIsExpanded = true
                            keyboardController?.hide()
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
                ExposedDropdownMenu(
                    expanded = dropDownIsExpanded,
                    onDismissRequest = {
                        dropDownIsExpanded = false
                    },
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
                                keyboardController?.hide()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DropDownTrailingIcon(expanded: Boolean, onClick: () -> Unit) {
    Icon(
        modifier = Modifier
            .clickable { onClick.invoke() }
            .padding(end = SettingsDefaults.TRAILING_ICON_END_PADDING)
            .rotate(if (expanded) 180f else 0f),
        imageVector = Icons.Filled.ArrowDropDown,
        contentDescription = null
    )
}

