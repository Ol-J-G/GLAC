package de.oljg.glac.core.util

import androidx.compose.runtime.Composable
import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.alarms.ui.utils.translate
import de.oljg.glac.clock.digital.ui.utils.DividerLineEnd
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.settings.clock.ui.utils.translate
import kotlin.reflect.KClass

@Composable
fun translateDropDownItemText(
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
    else -> defaultPrettyPrinter(itemValue)
}
