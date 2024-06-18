package de.oljg.glac.core.alarms.media.utils

import android.content.Context
import androidx.core.net.toUri
import de.oljg.glac.core.alarms.media.utils.AlarmSoundDefaults.MP3_FILE_EXTENSION
import de.oljg.glac.core.alarms.media.utils.AlarmSoundDefaults.OGG_FILE_EXTENSION
import de.oljg.glac.core.alarms.media.utils.AlarmSoundDefaults.WAV_FILE_EXTENSION
import de.oljg.glac.core.util.CommonFileDefaults.FILE_EXTENSION_DELIMITER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


suspend fun getSoundFileUrisFromFilesDir(context: Context): List<String> {
    return withContext(Dispatchers.IO) {
        context.filesDir.listFiles()
            ?.filter { file -> file.isValidSoundFile() }
            ?.map { soundFile -> soundFile.toUri().toString() }
            ?: emptyList()
    }
}

fun File.isValidSoundFile(): Boolean {
    return this.canRead() && this.isFile &&
            (this.name.hasMp3Extension()
                    || this.name.hasWavExtension()
                    || this.name.hasOggExtension())
}

fun String.hasMp3Extension(): Boolean =
        this.endsWith("${FILE_EXTENSION_DELIMITER}${MP3_FILE_EXTENSION}", ignoreCase = true)

fun String.hasWavExtension(): Boolean =
        this.endsWith("${FILE_EXTENSION_DELIMITER}${WAV_FILE_EXTENSION}", ignoreCase = true)

fun String.hasOggExtension(): Boolean =
        this.endsWith("${FILE_EXTENSION_DELIMITER}${OGG_FILE_EXTENSION}", ignoreCase = true)


object AlarmSoundDefaults {
    const val ALARM_SOUND_URI_KEY = "alarm_sound_uri"

    private const val MIMETYPE_MP3 = "audio/mpeg"
    private const val MIMETYPE_WAV = "audio/wav"
    private const val MIMETYPE_OGG = "audio/ogg"
    val SOUND_MIMETYPES = arrayOf(MIMETYPE_MP3, MIMETYPE_WAV, MIMETYPE_OGG)

    const val MP3_FILE_EXTENSION = "mp3"
    const val OGG_FILE_EXTENSION = "ogg"
    const val WAV_FILE_EXTENSION = "wav"
}
