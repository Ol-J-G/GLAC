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
import androidx.compose.ui.unit.dp
import de.oljg.glac.core.util.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontWeightDropDown(
    label: String,
    selectedFontWeight: String,
    onNewFontWeightSelected: (String) -> Unit
) {
    var dropDownIsExpanded by remember {
        mutableStateOf(false)
    }

    val allFontWeightNames = listOf( //TODO: extract to utils object
        FontWeight.THIN,
        FontWeight.EXTRA_LIGHT,
        FontWeight.LIGHT,
        FontWeight.NORMAL,
        FontWeight.MEDIUM,
        FontWeight.SEMI_BOLD,
        FontWeight.BOLD,
        FontWeight.EXTRA_BOLD,
        FontWeight.BLACK
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(end = 4.dp),
            text = label,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(end = 4.dp),
                expanded = dropDownIsExpanded,
                onExpandedChange = { dropDownIsExpanded = !dropDownIsExpanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(.5f)
                        .clickable(onClick = { dropDownIsExpanded = true }),
                    value = selectedFontWeight,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropDownIsExpanded) }
                )

                ExposedDropdownMenu(
                    expanded = dropDownIsExpanded,
                    onDismissRequest = { dropDownIsExpanded = false }
                ) {
                    allFontWeightNames.forEach { fontWeight ->
                        DropdownMenuItem(
                            text = { Text(fontWeight.name) },
                            onClick = {
                                onNewFontWeightSelected(fontWeight.name)
                                dropDownIsExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

