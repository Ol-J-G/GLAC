package de.oljg.glac.feature_alarm.ui.utils

import android.net.Uri
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

object AlarmDefaults {
    //- UI defaults start --------------------------------------------------------------------------
    val ALARM_DIALOG_ACTIONS_VERTICAL_PADDING = 8.dp
    val ALARM_DIALOG_ACTIONS_BUTTON_SPACE = 0.dp
    val ALARM_DIALOG_ACTIONS_END_PADDING = 12.dp

    val ALARM_DIALOG_LABEL_START_PADDING = 12.dp
    val ALARM_DIALOG_HORIZONTAL_PADDING = 16.dp
    val ALARM_DIALOG_VERTICAL_PADDING = 16.dp

    const val PREVIEW_PLAYER_CORNER_SIZE_PERCENT = 50
    val PREVIEW_PLAYER_ELEVATION = 8.dp
    val PREVIEW_PLAYER_PADDING = 8.dp
    val PREVIEW_PLAYER_ICON_SIZE = 44.dp

    val ALARM_REACTION_DIALOG_PADDING = 16.dp
    const val ALARM_REACTION_DIALOG_BUTTON_WEIGHT = 1.7f
    const val ALARM_REACTION_DIALOG_DISMISS_BUTTON_WEIGHT = 1f
    val ALARM_REACTION_DIALOG_BUTTON_SHAPE = RoundedCornerShape(24.dp)

    val DIALOG_MESSAGE_TOP_PADDING = 8.dp

    val TIME_PICKER_DIALOG_TONAL_ELEVATION = 6.dp
    val TIME_PICKER_DIALOG_PADDING = 24.dp
    val TIME_PICKER_DIALOG_TITLE_BOTTOM_PADDING = 20.dp
    val TIME_PICKER_DIALOG_ACTIONS_HEIGHT = 40.dp

    val ALARM_COUNTDOWN_HORIZONTAL_PADDING = 12.dp
    val ALARM_COUNTDOWN_VERTICAL_PADDING = 8.dp
    val ALARM_COUNTDOWN_HEIGHT = 48.dp

    val ALARM_LIST_ITEM_PADDING = 8.dp
    val ALARM_LIST_ITEM_HORIZONTAL_PADDING = 12.dp
    val ALARM_LIST_ITEM_SURFACE_CORNER_SIZE = 8.dp
    val ALARM_LIST_ITEM_BORDER_SIZE = 2.dp
    val ALARM_LIST_ITEM_TEXT_ICON_SPACE = 8.dp
    val ALARM_LIST_ITEM_ROW_HEIGHT = 48.dp

    val ALARMS_LIST_SCREEN_HORZONTAL_PADDING = 8.dp

    val ALARM_SOUND_SELECTOR_TOP_PADDING = 24.dp
    val ALARM_SOUND_SELECTOR_BOTTOM_PADDING = 0.dp

    val IMPORT_ALARM_SOUND_ICON_SIZE = 44.dp

    val MOMENT_SELECTOR_PADDING = 16.dp
    val REPETITION_SELECTOR_TOP_PADDING = 16.dp

    val ALARM_SETTINGS_SCREEN_PADDING = 16.dp
    val ALARM_SETTINGS_SCREEN_VERTICAL_SPACE = 16.dp
    val ALARM_SETTINGS_SCREEN_HORIZONTAL_SPACE = 8.dp
    //- UI defaults end ----------------------------------------------------------------------------

    val MIN_LIGHT_ALARM_DURATION = 1.minutes
    val MAX_LIGHT_ALARM_DURATION = 60.minutes

    val MIN_SNOOZE_DURATION = 5.minutes
    val MAX_SNOOZE_DURATION = 60.minutes

    val MIN_ALARM_SOUND_FADE_DUARTION = Duration.ZERO
    val MAX_ALARM_SOUND_FADE_DUARTION = 60.seconds

    // Users can schedule an alarm from now + ALARM_START_BUFFER
    val ALARM_START_BUFFER = 2.minutes

    val REPEAT_MODES = Repetition.entries.map { repeatMode -> repeatMode.name }

    val localizedFullDateFormatter: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)

    val localizedShortTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

    val localizedShortDateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    val minutesSaver: Saver<Duration, Any> = run {
        val minutes = "minutes"
        mapSaver(
            save = { duration -> mapOf(minutes to duration.toInt(DurationUnit.MINUTES)) },
            restore = { minutesMap -> (minutesMap[minutes] as Int).minutes }
        )
    }

    val uriSaver: Saver<Uri, Any> = run {
        val uriKey = "uri"
        mapSaver(
            save = { uri -> mapOf(uriKey to uri.toString()) },
            restore = { uriMap -> Uri.parse(uriMap[uriKey].toString()) }
        )
    }

    val localDateTimeSaver: Saver<LocalDateTime?, Any> = run {
        val localDateTimeKey = "localDateTime"
        mapSaver(
            save = { localDateTime ->
                mapOf(localDateTimeKey to localDateTime?.format(DateTimeFormatter.ISO_DATE_TIME))
            },
            restore = { localDateTimeMap ->
                localDateTimeMap[localDateTimeKey]?.let {
                    LocalDateTime.parse(it as String, DateTimeFormatter.ISO_DATE_TIME)
                }
            }
        )
    }
}
