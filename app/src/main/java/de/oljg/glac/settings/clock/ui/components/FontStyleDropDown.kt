package de.oljg.glac.settings.clock.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DROPDOWN_END_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DROPDOWN_ROW_VERTICAL_PADDING

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontStyleDropDown(
    label: String,
    selectedFontStyle: String,
    onNewFontStyleSelected: (String) -> Unit
) {
    var dropDownIsExpanded by remember {
        mutableStateOf(false)
    }

    val allFontStyleNames = listOf(
        FontStyle.NORMAL,
        FontStyle.ITALIC
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DROPDOWN_ROW_VERTICAL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(end = DROPDOWN_END_PADDING),
            text = label,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(end = DROPDOWN_END_PADDING),
                expanded = dropDownIsExpanded,
                onExpandedChange = { dropDownIsExpanded = !dropDownIsExpanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(.5f)
                        .clickable(onClick = { dropDownIsExpanded = true }),
                    value = selectedFontStyle,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropDownIsExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = dropDownIsExpanded,
                    onDismissRequest = { dropDownIsExpanded = false }
                ) {
                    allFontStyleNames.forEach { fontStyle ->
                        DropdownMenuItem(
                            text = { Text(fontStyle.name) },
                            onClick = {
                                onNewFontStyleSelected(fontStyle.name)
                                dropDownIsExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

