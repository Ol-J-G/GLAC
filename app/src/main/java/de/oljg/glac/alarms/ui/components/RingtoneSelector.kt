package de.oljg.glac.alarms.ui.components

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R


@Composable
fun RingtoneSelector(onRingtoneSelected: (Uri) -> Unit) {
    val ringTonePickerintent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
    ringTonePickerintent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL)
    ringTonePickerintent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
        stringResource(R.string.please_select_alarm_sound) + ":")
    ringTonePickerintent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
    ringTonePickerintent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)

    val ringtonePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultData = result.data

        when(result.resultCode) {
            Activity.RESULT_OK -> {

                // In case of RESULT_OK result.data Intent should be not null! => !! is save
                val ringToneUri = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                        resultData!!.getParcelableExtra(
                            RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
                            Uri::class.java
                        )!!
                    }
                    else -> { // < Build.VERSION_CODES.TIRAMISU (33) //TODO: test
                        resultData!!.getParcelableExtra(
                            RingtoneManager.EXTRA_RINGTONE_PICKED_URI
                        )!!
                    }
                }
                onRingtoneSelected(ringToneUri)
            }
        }
    }

    TextButton(onClick = { ringtonePicker.launch(ringTonePickerintent) }) {
        Text(stringResource(R.string.select_ringtone).uppercase())
    }
}
