package de.oljg.glac.clock.digital.ui.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import de.oljg.glac.clock.digital.ui.utils.ScreenSizeDefaults.DEFAULT_MAX_SCREEN_HEIGHT_COMPACT
import de.oljg.glac.clock.digital.ui.utils.ScreenSizeDefaults.DEFAULT_MAX_SCREEN_HEIGHT_MEDIUM
import de.oljg.glac.clock.digital.ui.utils.ScreenSizeDefaults.DEFAULT_MAX_SCREEN_WIDTH_COMPACT
import de.oljg.glac.clock.digital.ui.utils.ScreenSizeDefaults.DEFAULT_MAX_SCREEN_WIDTH_MEDIUM


@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }


data class PreviewState(
    var currentTimeStringLength: Int = 0,
    var currentFont: String = "",
    var currentFontWeight: String = "",
    var currentFontStyle: String = "",
    var currentDividerStyle: String = "",
    var currentDividerThickness: Int = 0
)



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
 * @see (https://stackoverflow.com/questions/69688138/how-to-hide-navigationbar-and-statusbar-in-jetpack-compose)
 */
@Composable
fun HideSystemBars() {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val window = context.findActivity()?.window ?: return@DisposableEffect onDispose {}
        val insetsController =
            WindowCompat.getInsetsController(window, window.decorView)

        insetsController.apply {
            hide(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            insetsController.apply {
                show(WindowInsetsCompat.Type.statusBars())
                show(WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
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
