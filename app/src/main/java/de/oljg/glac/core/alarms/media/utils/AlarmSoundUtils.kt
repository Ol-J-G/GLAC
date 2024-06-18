package de.oljg.glac.core.alarms.media.utils

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import de.oljg.glac.core.alarms.media.utils.AlarmSoundDefaults.MP3_FILE_EXTENSION
import de.oljg.glac.core.alarms.media.utils.AlarmSoundDefaults.OGG_FILE_EXTENSION
import de.oljg.glac.core.alarms.media.utils.AlarmSoundDefaults.SOUNDS_ASSETS_DIRECTORY
import de.oljg.glac.core.alarms.media.utils.AlarmSoundDefaults.WAV_FILE_EXTENSION
import de.oljg.glac.core.util.CommonFileDefaults.FILE_EXTENSION_DELIMITER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


/**
 * Copies a file from project's assets folder (exactly [relativeAssetDirectory]/[assetFileName]) to
 * app's cache folder (context.cacheDir), but only in case it does not exist there.
 *
 * In case the asset file already exists in cache, only asset file cache URI will be returned.
 *
 * Example:
 * asset file => app/src/main/assets/sounds/example.mp3
 * [relativeAssetDirectory] => sounds
 * [assetFileName] => example.mp3
 * asset file cache URI
 * => file:///data/user/0/de.oljg.glac/cache/example.mp3
 * => this URI is a valid input to use with [android.media.MediaPlayer.create]
 *
 *
 *
 * This seems to be necessary in order to get a valid file URI, as input for MediaPlayer (
 * used with URIs in [de.oljg.glac.core.alarms.media.service.AlarmSoundService]).
 *
 * Unfortunately, I didn't find a more convenient way to play sound files from assets :/
 * Alternatively, playing sound from resources (which could also be used in
 * a [android.media.MediaPlayer.create] overloaded function), would also be possible, but then,
 * how to get a list of all sound resource files? (short research revealed, that this seems to be
 * actually not possible, unless using reflection ... *sigh* ...)
 *
 * Source (slightly modified to fit GLAC requirements):
 * https://stackoverflow.com/questions/4820816/how-to-get-uri-from-an-asset-file
 */
@Throws(IOException::class)
private suspend fun getAssetFileFromCache(
    context: Context,
    relativeAssetDirectory: String,
    assetFileName: String
): Uri = File(context.cacheDir, assetFileName)
            .also {
                if (!it.exists()) {
                    withContext(Dispatchers.IO) {
                        it.outputStream().use { cache ->
                            context.assets.open(relativeAssetDirectory
                                    + File.separator.toString()
                                    + assetFileName
                            ).use { inputStream ->
                                inputStream.copyTo(cache)
                            }
                        }
                    }
                }
            }.toUri()


private suspend fun getSoundFileNamesFromAssets(context: Context): List<String> {
    return withContext(Dispatchers.IO) {
        context.assets.list(SOUNDS_ASSETS_DIRECTORY)?.toList()?.filterNotNull()
            ?: emptyList()
    }
}


suspend fun getAlarmSoundFileUrisFromCache(context: Context): List<String> {
    return buildList {
        addAll(
            getSoundFileNamesFromAssets(context).map { soundFileName ->
                getAssetFileFromCache(context, SOUNDS_ASSETS_DIRECTORY, soundFileName).toString()
            }
        )
    }
}


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

    const val SOUNDS_ASSETS_DIRECTORY = "sounds"
    const val GLAC_PREFIX = "GLAC_" // used as filename prefix (built-in alarm sounds in assets)
}
