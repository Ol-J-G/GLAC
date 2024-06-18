package de.oljg.glac.core.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import de.oljg.glac.core.util.CommonFileDefaults.FILE_EXTENSION_DELIMITER
import de.oljg.glac.core.util.CommonFileDefaults.PATH_SEPARATOR
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults.LOG_TAG
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults.UNKNOWN_FILENAME
import de.oljg.glac.settings.clock.ui.utils.isFileUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**
 * Used to import font/sound files from users device. This way, a user can import a font file, and
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

fun removeLocalFile(uri: Uri): Boolean {
    if (!uri.toString().isFileUri()) return false
    return uri.toFile().delete()
}

fun String.cutOffFileNameExtension(): String = this.substringBeforeLast(FILE_EXTENSION_DELIMITER)

fun String.cutOffPathFromUri(): String = this.substringAfterLast(PATH_SEPARATOR)

object CommonFileDefaults {
    val PATH_SEPARATOR = File.separator.toString()
    const val FILE_EXTENSION_DELIMITER = '.'
    const val FILE_PROTOCOL = "file://"
}
