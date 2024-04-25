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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.oljg.glac.settings.clock.ui.utils.cutOffPathfromFontUri
import de.oljg.glac.settings.clock.ui.utils.getFontFileNamesFromAssets
import de.oljg.glac.settings.clock.ui.utils.getFontFileUrisFromFilesDir
import de.oljg.glac.settings.clock.ui.utils.prettyPrintFontName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontDropDown(
    label: String,
    selectedFont: String,
    onNewFontSelected: (String) -> Unit,
    onNewFontImported: (String) -> Unit
) {
    val context = LocalContext.current
    var allFontFileNamesAndUris by remember {
        mutableStateOf(emptyList<String>())
    }
    var fontFileNamesFromAssets by remember {
        mutableStateOf(emptyList<String>())
    }
    var fontFileUrisFromFilesDir by remember {
        mutableStateOf(emptyList<String>())
    }

    // Only asynchronously (re-)populate lists initially or when they change ...
    LaunchedEffect(Unit) {
        fontFileNamesFromAssets = getFontFileNamesFromAssets(context)
        fontFileUrisFromFilesDir = getFontFileUrisFromFilesDir(context)

        /**
         * Merge font file names from assets (builtin fonts) and import font file URIs from local
         * storage's files directory, and finally sort it alphabetically.
         */
        allFontFileNamesAndUris = (fontFileNamesFromAssets + fontFileUrisFromFilesDir).sortedWith(
            compareBy(String.CASE_INSENSITIVE_ORDER) { it.cutOffPathfromFontUri() }
        )
    }

    var dropDownIsExpanded by remember {
        mutableStateOf(false)
    }

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
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImportFontButton(onNewFontImported = onNewFontImported)
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(end = 4.dp),
                expanded = dropDownIsExpanded,
                onExpandedChange = { dropDownIsExpanded = !dropDownIsExpanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(.85f)
                        .clickable(onClick = { dropDownIsExpanded = true }),
                    value = selectedFont.prettyPrintFontName(),
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = { TrailingIcon(expanded = dropDownIsExpanded) }
                )

                ExposedDropdownMenu(
                    expanded = dropDownIsExpanded,
                    onDismissRequest = { dropDownIsExpanded = false }
                ) {
                    allFontFileNamesAndUris.forEach { fontFileNameOrUri ->
                        DropdownMenuItem(
                            text = { Text(fontFileNameOrUri.prettyPrintFontName()) },
                            onClick = {
                                onNewFontSelected(fontFileNameOrUri)
                                dropDownIsExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
