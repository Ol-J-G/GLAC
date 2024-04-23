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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontDropDown(
    label: String,
    selectedFont: String,
    onNewFontSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val fontFileNames = context.assets.list("fonts")?.toList()?.filterNotNull()
        ?: emptyList()

    var isExpanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(end = 20.dp)
                .fillMaxWidth(.4f),
            text = label,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .clickable(onClick = { isExpanded = true }),
                //TODO: make string better looking, like: d_din_bold -> D Din Bold | d_din_regular -> D Din
                value = selectedFont.cutOffFileNameExtension(),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = { TrailingIcon(expanded = isExpanded) }
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                fontFileNames.forEach { fontFileName ->
                    DropdownMenuItem(
                        text = { Text(text = fontFileName.cutOffFileNameExtension()) },
                        onClick = {
                            onNewFontSelected(fontFileName)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}


fun String.cutOffFileNameExtension(): String = this.substringBefore('.')

