package de.oljg.glac.core.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import de.oljg.glac.core.util.ScreenSizeDefaults.DEFAULT_MAX_SCREEN_HEIGHT_COMPACT
import de.oljg.glac.core.util.ScreenSizeDefaults.DEFAULT_MAX_SCREEN_HEIGHT_MEDIUM
import de.oljg.glac.core.util.ScreenSizeDefaults.DEFAULT_MAX_SCREEN_WIDTH_COMPACT
import de.oljg.glac.core.util.ScreenSizeDefaults.DEFAULT_MAX_SCREEN_WIDTH_MEDIUM
import de.oljg.glac.feature_alarm.domain.media.utils.prettyPrintAlarmSoundUri
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import de.oljg.glac.feature_alarm.ui.utils.translate
import de.oljg.glac.feature_clock.ui.clock.utils.DividerLineEnd
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentWeight
import de.oljg.glac.feature_clock.ui.settings.utils.translate
import kotlin.reflect.KClass

@Composable
fun translateDropDownItemText(
    type: KClass<out Any>,
    itemValue: String,
    defaultPrettyPrinter: (String) -> String
) = when(type) {
    FontStyle::class -> FontStyle.valueOf(itemValue).translate()
    FontWeight::class -> FontWeight.valueOf(itemValue).translate()
    SevenSegmentStyle::class -> SevenSegmentStyle.valueOf(itemValue).translate()
    SevenSegmentWeight::class -> SevenSegmentWeight.valueOf(itemValue).translate()
    DividerStyle::class -> DividerStyle.valueOf(itemValue).translate()
    DividerLineEnd::class -> DividerLineEnd.valueOf(itemValue).translate()
    Repetition::class -> Repetition.valueOf(itemValue).translate()
    else -> {
        if (itemValue == Settings.System.DEFAULT_RINGTONE_URI.toString())
            Uri.parse(itemValue).prettyPrintAlarmSoundUri()
        else
            defaultPrettyPrinter(itemValue)

    }
}


fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}


/**
 * Inspired by PLs code (but changed it just a bit):
 * https://github.com/philipplackner/SupportAllScreenSizesCompose/
 *
 * Default window size values taken from:
 * https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes#kotlin
 */
@Composable
fun screenDetails(): ScreenDetails {
    val configuration = LocalConfiguration.current
    return ScreenDetails(
        screenWidthType = when {
            configuration.screenWidthDp < DEFAULT_MAX_SCREEN_WIDTH_COMPACT
            -> ScreenDetails.DisplayType.Compact

            configuration.screenWidthDp < DEFAULT_MAX_SCREEN_WIDTH_MEDIUM
            -> ScreenDetails.DisplayType.Medium

            else -> ScreenDetails.DisplayType.Expanded
        },
        screenHeightType = when {
            configuration.screenHeightDp < DEFAULT_MAX_SCREEN_HEIGHT_COMPACT
            -> ScreenDetails.DisplayType.Compact

            configuration.screenHeightDp < DEFAULT_MAX_SCREEN_HEIGHT_MEDIUM
            -> ScreenDetails.DisplayType.Medium

            else -> ScreenDetails.DisplayType.Expanded
        },
        screenWidth = configuration.screenWidthDp.dp,
        screenHeight = configuration.screenHeightDp.dp,
        screenSize = IntSize(configuration.screenWidthDp, configuration.screenHeightDp)
    )
}

data class ScreenDetails(
    val screenWidthType: DisplayType,
    val screenHeightType: DisplayType,
    val screenWidth: Dp,
    val screenHeight: Dp,
    val screenSize: IntSize
) {
    sealed class DisplayType {
        data object Compact : DisplayType()
        data object Medium : DisplayType()
        data object Expanded : DisplayType()
    }
}

object ScreenSizeDefaults {
    const val DEFAULT_MAX_SCREEN_WIDTH_COMPACT = 600
    const val DEFAULT_MAX_SCREEN_WIDTH_MEDIUM = 840
    const val DEFAULT_MAX_SCREEN_HEIGHT_COMPACT = 480
    const val DEFAULT_MAX_SCREEN_HEIGHT_MEDIUM = 900

    // Measured from default Android Studio emulators
    val MAX_SCREEN_WIDTH_SMALL_DEVICE_PORTRAIT = 360.dp
    val MAX_SCREEN_WIDTH_MEDIUM_DEVICE_PORTRAIT = 411.dp
    val MAX_SCREEN_WIDTH_SMALL_DEVICE_LANDSCAPE = 640.dp
    val MAX_SCREEN_WIDTH_MEDIUM_DEVICE_LANDSCAPE = 914.dp
}


/**
 * activity.window.attributes.screenBrightness returns by default
 * WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE (-1) => what user set up.
 *
 * To get the device's actual screen brightness a user has been set, it seems to be necessary to
 * use Settings.System.SCREEN_BRIGHTNESS.
 *
 * To use this value (int, 0..255 / dark..bright) as input for [setScreenBrightness] it needs
 * to be converted to Float (0f..1f / dark..bright).
 */
fun getScreenBrightness(context: Context) = Settings.System.getInt(
    context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, -1
) / 255f


fun setScreenBrightness(activity: Activity, brightness: Float) {
    activity.window.attributes = activity.window.attributes.apply {
        screenBrightness = brightness
    }
}


fun resetScreenBrightness(activity: Activity) {
    setScreenBrightness(activity, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) // -1f
}


/**
 * Lock current screen orientation (user cannot rotate) until alarm is stopped/snoozed.
 * Otherwise, when a user would rotate device during alarm process, animations
 * would be restarted, which would bring the alarm process timeline in an inconsistent state!
 *
 * Of course, nevertheless this is unfortunately a 'dirty' workaround.
 *
 * But, assuming this is a rare case, it's not worth to take care about pause/continue animation
 * on screen rotation, because it's way too complicated imho (write a Savable for
 * Animatable<Color, AnimationVector4D> to use rememberSavable? => uh-oh..uhm, dunno
 * how to do this yet...maybe trying it sometimes).
 *
 * And, usually, a user snoozes or stops the alarm, rather than grap and rotate the device
 * beforehand (at least when user is about to wake up and still tired => don't move too
 * much then, right? :>).
 */
fun lockScreenOrientation(context: Context, currentOrientation: Int) {
    val activity = context.findActivity()
    activity?.let {
        it.requestedOrientation = when (currentOrientation) {
            Configuration.ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
}

// Alarm is stopped/snoozed => Unlock screen orientation => let user rotate again
fun unlockScreenOrientation(activity: Activity?) {
    activity?.let {
        it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED // -1
    }
}

object CommonUtils {
    const val SPACE = ' '
    val ALERT_DIALOG_PADDING = 16.dp
    val ALERT_DIALOG_EDGE_PADDING = 12.dp
}
