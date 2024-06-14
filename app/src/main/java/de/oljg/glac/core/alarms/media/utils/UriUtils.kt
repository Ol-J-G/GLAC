package de.oljg.glac.core.alarms.media.utils

import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.SPACE
import de.oljg.glac.core.alarms.media.utils.UriDefaults.RINGTONE_TITLE_KEY
import kotlin.random.Random

@Composable
fun Uri.prettyPrintRingtone() : String {
    if (this == Settings.System.DEFAULT_RINGTONE_URI) return stringResource(R.string._default)
    return this.getQueryParameter(RINGTONE_TITLE_KEY)
        ?: (stringResource(R.string.no_title) + SPACE + Random.nextInt().toString())
}

object UriDefaults {
    const val RINGTONE_TITLE_KEY = "title"
}
