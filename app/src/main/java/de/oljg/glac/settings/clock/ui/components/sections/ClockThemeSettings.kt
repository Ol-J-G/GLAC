package de.oljg.glac.settings.clock.ui.components.sections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.clock.data.ClockSettings
import de.oljg.glac.core.clock.data.ClockTheme
import de.oljg.glac.core.ui.components.SettingsSection
import de.oljg.glac.core.util.CommonClockUtils.DEFAULT_THEME_NAME
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_ICON_BUTTON_SIZE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.MAX_THEME_NAME_LENGTH
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.MIN_THEME_NAME_LENGTH
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.RESET_BUTTON_SIZE
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockThemeSettings(viewModel: ClockSettingsViewModel = hiltViewModel()) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value

    var textFieldValue by remember(clockSettings.clockThemeName) {
        mutableStateOf(clockSettings.clockThemeName)
    }
    var isValidInput by remember {
        mutableStateOf(true)
    }

    SettingsSection(
        sectionTitle = stringResource(R.string.theme),
        expanded = clockSettings.clockSettingsSectionThemeIsExpanded,
        onExpandedChange = { expanded ->
            coroutineScope.launch {
                viewModel.updateClockSettings(
                    clockSettings.copy(
                        clockSettingsSectionThemeIsExpanded = expanded
                    )
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        DropDownSelector(
            label = stringResource(R.string.theme_name),
            selectedValue = textFieldValue,
            onNewValueSelected = { selectedThemeName ->
                coroutineScope.launch {
                    viewModel.updateClockSettings(
                        clockSettings.copy(clockThemeName = selectedThemeName)
                    )
                }
                textFieldValue = selectedThemeName
                isValidInput = true
            },
            values = clockSettings.themes.keys.toList(),
            readOnly = false,
            onTextFieldValueChanged = { newValue ->
                textFieldValue = newValue
                isValidInput = textFieldValue.isNotBlank()
                        && textFieldValue.length <= MAX_THEME_NAME_LENGTH
            },
            supportingText = {
                if (!isValidInput)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = when {
                            textFieldValue.isBlank() -> stringResource(R.string.please_enter_a_name)
                            textFieldValue.length > MAX_THEME_NAME_LENGTH ->
                                "${stringResource(R.string.invalid_lenght)}! " +
                                        "${stringResource(R.string.allowed)}: " +
                                        "$MIN_THEME_NAME_LENGTH - $MAX_THEME_NAME_LENGTH"

                            else -> ""
                        },
                        color = MaterialTheme.colorScheme.error
                    )
            },
            resetValueComponent = {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy( // reset current theme to default theme
                                    themes = clockSettings.themes.put(textFieldValue, ClockTheme())
                                )
                            )
                        }
                    },
                    enabled = clockSettings.themes.containsKey(textFieldValue)
                            && clockSettings.themes[textFieldValue] != ClockTheme(),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(RESET_BUTTON_SIZE),
                        imageVector = Icons.Filled.RestartAlt,
                        contentDescription = stringResource(R.string.reset_theme)
                    )
                }
            },
            removeValueComponent = {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy(

                                    /**
                                     * Remove theme and select default theme afterwards.
                                     *
                                     * Note that clockSettings.themes.remove(key) did NOT remove
                                     * the theme entry (dunno why!??..containsKey(key) was true..
                                     * oO), so, alternatively, build a new map without the
                                     * entry to remove...
                                     */
                                    themes = buildMap {
                                        clockSettings.themes.entries.forEach { (key, value) ->
                                            if (key != textFieldValue) put(key, value)
                                        }
                                    }.toPersistentMap(),
                                    clockThemeName = DEFAULT_THEME_NAME
                                )
                            )
                            textFieldValue = DEFAULT_THEME_NAME
                        }
                    },
                    enabled = textFieldValue != DEFAULT_THEME_NAME
                            && clockSettings.themes.containsKey(textFieldValue),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(DEFAULT_ICON_BUTTON_SIZE),
                        imageVector = Icons.Filled.RemoveCircleOutline,
                        contentDescription = stringResource(R.string.remove_theme)
                    )
                }
            },
            addValueComponent = {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.updateClockSettings(
                                clockSettings.copy( // create a new theme based on current theme
                                    themes = clockSettings.themes.put(
                                        textFieldValue, clockSettings.themes.getOrDefault(
                                            key = clockSettings.clockThemeName,
                                            defaultValue = ClockTheme()
                                        )
                                    ),
                                    clockThemeName = textFieldValue
                                )
                            )
                        }
                        keyboardController?.hide()
                    },
                    enabled = textFieldValue != DEFAULT_THEME_NAME
                            && !clockSettings.themes.containsKey(textFieldValue)
                            && isValidInput,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(DEFAULT_ICON_BUTTON_SIZE),
                        imageVector = Icons.Filled.AddCircleOutline,
                        contentDescription = stringResource(R.string.add_theme)
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
    }
}

