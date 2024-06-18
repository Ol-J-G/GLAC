package de.oljg.glac.core.alarms.media.utils

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.SPACE
import de.oljg.glac.core.alarms.media.utils.RingtoneDefaults.RINGTONE_TITLE_KEY
import de.oljg.glac.core.util.cutOffFileNameExtension
import de.oljg.glac.core.util.cutOffPathFromUri
import de.oljg.glac.settings.clock.ui.utils.isFileUri
import kotlin.random.Random


fun getAvailableRingtoneUris(context: Context): List<String> {
    val ringtoneManager = RingtoneManager(context)
    ringtoneManager.setType(RingtoneManager.TYPE_ALL)
    val cursor = ringtoneManager.cursor

    return buildList {
        while (cursor.moveToNext()) {
            add(ringtoneManager.getRingtoneUri(cursor.position).toString())
        }
    }
}


@Composable
fun Uri.prettyPrintRingtone(): String {
    // Default ringtone seems to have no title!? => calling it simply "Default Ringtone"
    if (this == Settings.System.DEFAULT_RINGTONE_URI)
        return stringResource(R.string._default_ringtone)
    return this.getQueryParameter(RINGTONE_TITLE_KEY)
        ?: (stringResource(R.string.no_title) + SPACE + Random.nextInt().toString())
}

private fun String.prettyPrintRingtoneUriString(): String {
    val uri = Uri.parse(this)
    // Can actually not be null => no translation necessary yet //TODO: test on other devices
    return uri.getQueryParameter(RINGTONE_TITLE_KEY) ?: "No Title"
}

/**
 * Just to use in one "special case".
 *
 * Well, wanted to make it a @Composable, but:
 * "Function References of @Composable functions are not currently supported"
 * @see de.oljg.glac.alarms.ui.components.AlarmSoundSelector
 */
fun String.prettyPrintAlarmSound() = when {
    this.isFileUri() -> this.cutOffFileNameExtension().cutOffPathFromUri()
    else -> this.prettyPrintRingtoneUriString()
}


object RingtoneDefaults {
    const val RINGTONE_TITLE_KEY = "title"
}
