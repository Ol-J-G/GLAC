package de.oljg.glac.feature_clock.ui.settings.utils

import android.content.Context
import androidx.core.net.toUri
import de.oljg.glac.core.util.CommonFileDefaults.FILE_EXTENSION_DELIMITER
import de.oljg.glac.core.util.CommonUtils.SPACE
import de.oljg.glac.core.util.cutOffFileNameExtension
import de.oljg.glac.core.util.cutOffPathFromUri
import de.oljg.glac.feature_clock.ui.clock.utils.FontDefaults
import de.oljg.glac.feature_clock.ui.settings.utils.FileUtilDefaults.FONT_ASSETS_DIRECTORY
import de.oljg.glac.feature_clock.ui.settings.utils.FileUtilDefaults.OPEN_TYPE_FONT_FILE_EXTENSION
import de.oljg.glac.feature_clock.ui.settings.utils.FileUtilDefaults.TRUE_TYPE_FONT_FILE_EXTENSION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


suspend fun getFontFileNamesFromAssets(context: Context): List<String> {
    return withContext(Dispatchers.IO) {
        context.assets.list(FONT_ASSETS_DIRECTORY)?.toList()?.filterNotNull()
            ?: emptyList()
    }
}

suspend fun getFontFileUrisFromFilesDir(context: Context): List<String> {
    return withContext(Dispatchers.IO) {
        context.filesDir.listFiles()
            ?.filter { file -> file.isValidFontFile() }
            ?.map { fontFile -> fontFile.toUri().toString() }
            ?: emptyList()
    }
}


fun String.replaceLast(char: Char, replacement: Char): String =
    this.substringBeforeLast(char) + replacement + this.substringAfterLast(char)

fun String.replaceLastWithBlank(char: Char): String =
    if (this.contains(char)) this.replaceLast(char, ' ') else this

fun String.prettyPrintFontName(): String {
    var result = this
        .cutOffPathFromUri()
        .cutOffFileNameExtension()
        .replaceLastWithBlank('_')
        .replace('-', SPACE)
        .replace(regex = Regex("(%20| )?[Rr]egular"), "")

    when {
        // For whatever reason '-' may not be part of asset file names ... oO
        result.startsWith("D_Din") -> result = result.replaceFirst('_', '-')

        // I seem to be too old to have fun with blanks as part of file names :>
        result.startsWith("Exo_2") -> result = result.replaceFirst('_', ' ')
    }
    return result
}


fun String.hasTrueTypeFontExtension(): Boolean =
    this.endsWith("$FILE_EXTENSION_DELIMITER$TRUE_TYPE_FONT_FILE_EXTENSION", ignoreCase = true)

fun String.hasOpenTypeFontExtension(): Boolean =
    this.endsWith("$FILE_EXTENSION_DELIMITER$OPEN_TYPE_FONT_FILE_EXTENSION", ignoreCase = true)

fun File.isValidFontFile(): Boolean {
    return this.canRead() && this.isFile &&
            (this.name.hasTrueTypeFontExtension() || this.name.hasOpenTypeFontExtension())
}


object FileUtilDefaults {
    const val TRUE_TYPE_FONT_FILE_EXTENSION = "ttf"
    const val OPEN_TYPE_FONT_FILE_EXTENSION = "otf"

    private const val MIMETYPE_TTF = "font/ttf"
    private const val MIMETYPE_OTF = "font/otf"
    val FONT_MIMETYPES = arrayOf(MIMETYPE_TTF, MIMETYPE_OTF)

    const val FONT_ASSETS_DIRECTORY = "fonts"

    val DEFAULT_FONT_NAMES: List<String>
        get() = listOf(
            FontDefaults.DEFAULT_MONOSPACE,
            FontDefaults.DEFAULT_SANS_SERIF,
            FontDefaults.DEFAULT_SERIF,
            FontDefaults.DEFAULT_CURSIVE
        )
}
