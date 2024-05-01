package de.oljg.glac.settings.clock.ui.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import de.oljg.glac.clock.digital.ui.utils.FontDefaults
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults.FILE_EXTENSION_DELIMITER
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults.FILE_PROTOCOL
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults.FONT_ASSETS_DIRECTORY
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults.LOG_TAG
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults.OPEN_TYPE_FONT_FILE_EXTENSION
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults.PATH_SEPARATOR
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults.TRUE_TYPE_FONT_FILE_EXTENSION
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults.UNKNOWN_FILENAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


/**
 * Used to import font files from users device. This way, a user can import a font file, and
 * even delete it afterwards from external storage, because it is copied to apps internal
 * storage.
 *
 * Takes an URI from document picker, creates a DocumentFile to obtain the filename
 * (didn't find a better solution yet), and finally copies the  document to a new file with the
 * same name in apps local storage files directory (context.filesDir).
 *
 * This approach seems not to need any permissions (by now, just tested with emulator and
 * files in Downloads folder...)
 */
@Throws(IOException::class)
suspend fun openDocumentAndSaveLocalCopy(context: Context, uri: Uri): File? {
    val fileName = DocumentFile.fromSingleUri(context, uri)?.name ?: UNKNOWN_FILENAME
    if (fileName == UNKNOWN_FILENAME) return null

    val localFileCopy = File(context.filesDir, fileName)
    withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                localFileCopy.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Could not save local copy of document '$uri': ${e.message}", e)
            e.printStackTrace()
        }
    }
    return localFileCopy
}

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


fun String.cutOffFileNameExtension(): String = this.substringBeforeLast(FILE_EXTENSION_DELIMITER)

fun String.cutOffPathfromFontUri(): String = this.substringAfterLast(PATH_SEPARATOR)

fun String.replaceLast(char: Char, replacement: Char): String =
    this.substringBeforeLast(char) + replacement + this.substringAfterLast(char)

fun String.replaceLastWithBlank(char: Char): String =
    if (this.contains(char)) this.replaceLast(char, ' ') else this

fun String.prettyPrintFontName(): String {
    return this
        .cutOffPathfromFontUri()
        .cutOffFileNameExtension()
        .replaceLastWithBlank('_')
        .replaceLastWithBlank('-')
        .replace(regex = Regex(" ?[Rr]egular"), "")
}

fun String.prettyPrintEnumName(): String {
    val words = this.lowercase().split('_')
    return buildString {
        words.forEachIndexed { index, word ->
            append(word.capitalize(Locale.current))

            /**
             * _Example_
             * Input        : "WORD_WORD"
             * Don't do this: "Word Word "
             * Do this      : "Word Word" (<= blank only between words)
             */
            if (index + 1 != words.size) append(' ')
        }
    }
}

fun String.hasTrueTypeFontExtension(): Boolean =
    this.endsWith("$FILE_EXTENSION_DELIMITER$TRUE_TYPE_FONT_FILE_EXTENSION", ignoreCase = true)

fun String.hasOpenTypeFontExtension(): Boolean =
    this.endsWith("$FILE_EXTENSION_DELIMITER$OPEN_TYPE_FONT_FILE_EXTENSION", ignoreCase = true)

fun String.isFileUri(): Boolean = this.startsWith(FILE_PROTOCOL)

fun File.isValidFontFile(): Boolean {
    return this.canRead() && this.isFile &&
            (this.name.hasTrueTypeFontExtension() || this.name.hasOpenTypeFontExtension())
}


object FileUtilDefaults {
    const val LOG_TAG = "FileUtils"
    const val UNKNOWN_FILENAME = "Unknown"

    const val PATH_SEPARATOR = '/'
    const val FILE_EXTENSION_DELIMITER = '.'
    const val FILE_PROTOCOL = "file://"

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

