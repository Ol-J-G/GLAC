package de.oljg.glac.settings.clock.ui.utils

import androidx.compose.ui.graphics.Color
import com.smarttoolfactory.extendedcolors.model.ColorItem

/**
 * Use "old" android Color class to parse (A)RGB color from hex code.
 * colorString = "FFFF0000" => Color.RED (opaque)
 */
fun Color.Companion.fromHexCode(colorString: String) =
    Color(android.graphics.Color.parseColor("#$colorString"))

/**
 * A = Alpha
 * R = Red
 * G = Green
 * B = Blue
 *
 * AARRGGBB => 8 places, 2 places hex value each
 * E.g.: Opaque Red => "FFFF0000" (which is Color.RED)
 */
fun String.isArgbHexCode() = this.matches(Regex("^[0-9A-Fa-f]{8}\$"))

/**
 * Returns apprpriate ARGB hex code for a Color.
 * E.g. Color.RED => "FFFF0000"
 *
 * Note: ColorItem.hexARGB returns '#' as first char => will be cut off, because it's not needed..
 */
fun Color.argbHexCode() = ColorItem(this).hexARGB.substring(1).uppercase()
