package de.oljg.glac.settings.clock.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontDropDown(
    label: String,
    selectedFont: String,
    onNewFontSelected: (String) -> Unit,
    onNewFontImported: (String) -> Unit
) {
    val context = LocalContext.current
    val fontFileNamesFromAssets =
        context.assets.list("fonts")?.toList()?.filterNotNull()
        ?: emptyList()

    val fontFileUrisFromFilesDir = context.filesDir.listFiles()?.filter { file ->
        file.canRead() && file.isFile &&
                (file.name.endsWith(".ttf") || file.name.endsWith(".otf")) //TODO: add const vals
    }?.map { fontFile -> fontFile.toUri().toString() }
        ?: emptyList()

    val allFontFileNamesAndUris = fontFileNamesFromAssets + fontFileUrisFromFilesDir

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
                        .fillMaxWidth(.75f)
                        .clickable(onClick = { dropDownIsExpanded = true }),
                    //TODO: make string better looking, like: d_din_bold -> D Din Bold | d_din_regular -> D Din (only if string contains '_')
                    value = selectedFont.formatFontUri().cutOffFileNameExtension(),
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
                            text = {
                                Text(
                                    text = fontFileNameOrUri.formatFontUri()
                                        .cutOffFileNameExtension()
                                )
                            },
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


@Composable
fun ImportFontButton(
    onNewFontImported: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val result = remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            result.value = it
        }

    IconButton(
        onClick = { launcher.launch(arrayOf("font/ttf", "font/otf")) } //TODO: introduce const vals
    ) {
        Icon(
            modifier = Modifier.size(22.dp),
            imageVector = Icons.Filled.Add,
            contentDescription = "Import Font" //TODO: add string res
        )
        result.value?.let { uri ->
            coroutineScope.launch {
                //TODO: introduce util suspend(?) fun "openDocumentAndSaveLocalCopy(context, uri): File
                val documentFile = DocumentFile.fromSingleUri(context, uri)
                val fileName = documentFile?.name ?: "Unknown"

                val bytes = context.contentResolver.openInputStream(uri)?.use {
                    it.readBytes()
                }
                val file = File(context.filesDir, fileName)
                FileOutputStream(file).use {
                    it.write(bytes)
                }
                onNewFontImported(file.toUri().toString())
            }
        }
    }
}


fun String.cutOffFileNameExtension(): String = this.substringBefore('.')

fun String.formatFontUri(): String = this.substringAfterLast('/')


