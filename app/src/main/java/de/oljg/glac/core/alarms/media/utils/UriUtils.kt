package de.oljg.glac.core.alarms.media.utils

import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.SPACE
import de.oljg.glac.core.alarms.media.utils.UriDefaults.RINGTONE_TITLE_KEY
import de.oljg.glac.core.util.CommonFileDefaults.FILE_PROTOCOL
import de.oljg.glac.core.util.cutOffFileNameExtension
import de.oljg.glac.core.util.cutOffPathFromUri
import kotlin.random.Random

@Composable
fun Uri.prettyPrintRingtone(): String {
    if (this == Settings.System.DEFAULT_RINGTONE_URI) return stringResource(R.string._default)
    return this.getQueryParameter(RINGTONE_TITLE_KEY)
        ?: (stringResource(R.string.no_title) + SPACE + Random.nextInt().toString())
}

private fun Uri.ringtoneTitle(): String {
    // Can actually not be null => no translation necessary yet //TODO: test on other devices
    return this.getQueryParameter(RINGTONE_TITLE_KEY) ?: "No Title"
}

fun String.prettyPrintRingtoneUriString(): String {
    val uri = Uri.parse(this)
    return uri.ringtoneTitle()
}

fun String.prettyPrintAlarmSound() = when {
    this.startsWith(FILE_PROTOCOL) -> {
        this.cutOffFileNameExtension().cutOffPathFromUri()
    }

    else -> {
        this.prettyPrintRingtoneUriString()
    }
}




object UriDefaults {
    const val RINGTONE_TITLE_KEY = "title"
}
