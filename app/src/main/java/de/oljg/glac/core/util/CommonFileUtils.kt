package de.oljg.glac.core.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import de.oljg.glac.core.util.CommonFileDefaults.FILE_EXTENSION_DELIMITER
import de.oljg.glac.core.util.CommonFileDefaults.FILE_PROTOCOL
import de.oljg.glac.core.util.CommonFileDefaults.LOG_TAG
import de.oljg.glac.core.util.CommonFileDefaults.PATH_SEPARATOR
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
    val fileName = DocumentFile.fromSingleUri(context, uri)?.name ?: return null

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

fun removeLocalFile(uriString: String): Boolean {
    try {
        val uri = Uri.parse(uriString)
        if (!uri.toString().isFileUri()) return false
        uri.toFile().delete()
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
    return true
}

fun String.cutOffFileNameExtension(): String = this.substringBeforeLast(FILE_EXTENSION_DELIMITER)

fun String.cutOffPathFromUri(): String = this.substringAfterLast(PATH_SEPARATOR)

fun String.isFileUri(): Boolean =
        this.startsWith(FILE_PROTOCOL, ignoreCase = true)

object CommonFileDefaults {
    val PATH_SEPARATOR = File.separator.toString()
    const val FILE_EXTENSION_DELIMITER = '.'
    const val FILE_PROTOCOL = "file://"

    const val LOG_TAG = "FileUtils"
}
