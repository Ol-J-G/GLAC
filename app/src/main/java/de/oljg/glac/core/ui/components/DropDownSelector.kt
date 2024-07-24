package de.oljg.glac.core.ui.components

import android.net.Uri
import android.provider.Settings
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.oljg.glac.core.utils.CoreLayoutDefaults.DROP_DOWN_SELECTOR_END_PADDING
import de.oljg.glac.core.utils.CoreLayoutDefaults.DROP_DOWN_SELECTOR_MENU_ITEM_FONT_SIZE
import de.oljg.glac.core.utils.CoreLayoutDefaults.DROP_DOWN_SELECTOR_ROW_VERTICAL_PADDING
import de.oljg.glac.core.utils.CoreLayoutDefaults.DROP_DOWN_SELECTOR_SPACE
import de.oljg.glac.core.utils.CoreLayoutDefaults.TRAILING_ICON_END_PADDING
import de.oljg.glac.core.utils.FontStyle
import de.oljg.glac.core.utils.FontWeight
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import de.oljg.glac.feature_alarm.ui.utils.prettyPrintAlarmSoundUri
import de.oljg.glac.feature_alarm.ui.utils.translate
import de.oljg.glac.feature_clock.ui.clock.utils.DividerLineEnd
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentWeight
import de.oljg.glac.feature_clock.ui.settings.utils.translate
import kotlin.reflect.KClass


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownSelector(
    label: String,
    selectedValue: String,
    onNewValueSelected: (String) -> Unit,
    values: List<String>,
    prettyPrintValue: (String) -> String = { value -> value },
    type: KClass<out Any> = String::class,
    maxWidthFraction: Float = 1f,
    readOnly: Boolean = true,
    topPadding: Dp = DROP_DOWN_SELECTOR_ROW_VERTICAL_PADDING,
    bottomPadding: Dp = DROP_DOWN_SELECTOR_ROW_VERTICAL_PADDING,
    startPadding: Dp = 0.dp,
    endPadding: Dp = 0.dp,
    onTextFieldValueChanged: (String) -> Unit = {},
    supportingText: @Composable () -> Unit = {},
    resetValueComponent: @Composable (() -> Unit)? = null,
    removeValueComponent: @Composable (() -> Unit)? = null,
    addValueComponent: @Composable (() -> Unit)? = null
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
            .padding(
                top = topPadding,
                bottom = bottomPadding,
                start = startPadding,
                end = endPadding
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(addValueComponent != null
                || removeValueComponent != null
                || resetValueComponent != null) {
                Row(
                    modifier = Modifier
                        .padding(end = DROP_DOWN_SELECTOR_END_PADDING * 3),
                    horizontalArrangement = Arrangement.spacedBy(
                        DROP_DOWN_SELECTOR_SPACE,
                        alignment = Alignment.Start
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    resetValueComponent?.invoke()
                    removeValueComponent?.invoke()
                    addValueComponent?.invoke()
                }
            }

            ExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(end = DROP_DOWN_SELECTOR_END_PADDING),
                expanded = dropDownIsExpanded,
                onExpandedChange = { dropDownIsExpanded = !dropDownIsExpanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(maxWidthFraction),
                    value = translateDropDownItemText(
                        type = type,
                        itemValue = selectedValue,
                        defaultPrettyPrinter = prettyPrintValue
                    ),
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
                                    text = translateDropDownItemText(
                                        type = type,
                                        itemValue = value,
                                        defaultPrettyPrinter = prettyPrintValue
                                    ),
                                    fontSize = DROP_DOWN_SELECTOR_MENU_ITEM_FONT_SIZE
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
            .padding(end = TRAILING_ICON_END_PADDING)
            .rotate(if (expanded) 180f else 0f),
        imageVector = Icons.Filled.ArrowDropDown,
        contentDescription = null
    )
}


@Composable
private fun translateDropDownItemText(
    type: KClass<out Any>,
    itemValue: String,
    defaultPrettyPrinter: (String) -> String
) = when(type) {
    FontStyle::class -> FontStyle.valueOf(itemValue).translate()
    FontWeight::class -> FontWeight.valueOf(itemValue).translate()
    SevenSegmentStyle::class -> SevenSegmentStyle.valueOf(itemValue).translate()
    SevenSegmentWeight::class -> SevenSegmentWeight.valueOf(itemValue).translate()
    DividerStyle::class -> DividerStyle.valueOf(itemValue).translate()
    DividerLineEnd::class -> DividerLineEnd.valueOf(itemValue).translate()
    Repetition::class -> Repetition.valueOf(itemValue).translate()
    else -> {
        if (itemValue == Settings.System.DEFAULT_RINGTONE_URI.toString())
            Uri.parse(itemValue).prettyPrintAlarmSoundUri()
        else
            defaultPrettyPrinter(itemValue)

    }
}
