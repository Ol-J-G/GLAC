package de.oljg.glac.alarms.ui.utils

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.util.findActivity
import de.oljg.glac.core.util.getScreenBrightness
import de.oljg.glac.core.util.setScreenBrightness

/**
 * "Cloud-Like" brush to let clock chars look like passing "dark clouds" during light alarm.
 *
 * Black and white gradient colors were chosen, because it fits in any light alarm color sequence.
 * Means, the clock chars are always visible (if clock chars would be, let's say, medium gray,
 * they could not be recognized against the background, when a color during light alarm
 * would be very close to medium grey...which leads to bad UX imho (imagine you're tired, almost
 * at sleep, but take a look to the clock, and you cannot see the time, what would make most
 * people rather angry, I guess :>))
 */
fun alarmBrush(offsetFactor: Float): ShaderBrush {
    return object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val xOffset = size.width * offsetFactor + size.width * 3
            val yOffset = size.height * offsetFactor - size.height * 2
            return RadialGradientShader(
                colors = listOf(Color.Black, Color.White),
                center = Offset(xOffset, yOffset),
                radius = maxOf(size.width, size.height),
                tileMode = TileMode.Mirror
            )
        }
    }
}


@Composable
fun animateAlarmBrushOffset(transition: InfiniteTransition) = transition.animateFloat(
    initialValue = -2f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
        tween(10000, easing = FastOutLinearInEasing),
        repeatMode = RepeatMode.Reverse
    ),
    label = "aBtAf"
)


@Composable
fun animateAlarmColor(transition: InfiniteTransition) = transition.animateColor(
    initialValue = Color.Black,
    targetValue = Color.White,
    animationSpec = infiniteRepeatable(
        animation = tween(500, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    ),
    label = "aCtAc"
)


@Composable
fun animateSnoozeAlarmIndicatorColor(
    transition: InfiniteTransition,
    initialColor: Color,
    targetColor: Color
) = transition.animateColor(
    initialValue = initialColor,
    targetValue = targetColor,
    animationSpec = infiniteRepeatable(
        animation = tween(5000, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    ),
    label = "aSaIc"
)


@Composable
private fun FadeBrightnessFromCurrentToFull(
    totalDurationMillis: Int,
    clockBrightness: Float? = null,
    maxSteps: Int = 100
) {
    require(maxSteps in 1..100)
    val context = LocalContext.current
    val activity = context.findActivity() ?: return

    // Fade animation initial value, by default device's current screen brightness
    val initialBrightness = clockBrightness ?: getScreenBrightness(context)

    /**
     * In case screen is (almost) on full brightness => no need to fade.
     * Furthermore, step calculation below would lead to a division by zero!
     * E.g.:
     * * OK  => 1f - .99f  = .01f  * 100 = 1f  toInt() => 1
     * * dbz => 1f - .991f = .009f * 100 = .9f toInt() => 0 !!! => BÄM :>
     */
    if (initialBrightness > .99f) return

    /**
     * 1f => Full brightness is the goal for default sunrise light alarm => "simulates" bright sun.
     *
     * But, whatever color the user chooses as the last color of the light alarm, the room in which
     * the alarm went off will definitely be a little brighter than without fading brightness at
     * all, except for very dark tones ofc (and, .. OLEDs!), ... hmm, don't really like
     * this flaw :/.
     * TODO_LATER: Think about introducing a setting to disable fading brightness during light alarm
     */
    val targetBrightness = 1f

    // 1..[maxSteps] steps (between brightness of 0f..0.99f / fulldark..almost_fullbright)
    val steps = ((targetBrightness - initialBrightness) * maxSteps).toInt()
    val step = targetBrightness / maxSteps
    var nextBrightness = initialBrightness + step
    val durationPerStep = totalDurationMillis / steps

    val brightness = remember { Animatable(initialBrightness) }
    LaunchedEffect(Unit) {
        repeat(steps) {
            brightness.animateTo(
                targetValue = nextBrightness,
                animationSpec = tween(durationMillis = durationPerStep, easing = LinearEasing)
            )
            setScreenBrightness(activity, brightness.value)
            nextBrightness += step
        }
    }
}


@Composable
fun LightAlarm(
    alarmToBeLaunched: Alarm,
    lightAlarmColors: List<Color>,
    lightAlarmAnimatedColor: Animatable<Color, AnimationVector4D>,
    clockBrightness: Float?,
    onFinished: () -> Unit
) {
    /**
     * Lock current screen orientation (user cannot rotate) until light alarm animation has ended.
     * Otherwise, when a user would rotate device during light alarm animation, the animation
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
    val currentOrientation = LocalConfiguration.current.orientation
    val activity = LocalContext.current.findActivity()
    activity?.let {
        it.requestedOrientation = when (currentOrientation) {
            Configuration.ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    val lightAlarmDurationMillis = alarmToBeLaunched.lightAlarmDuration.inWholeMilliseconds
    FadeBrightnessFromCurrentToFull(
        clockBrightness = clockBrightness,
        totalDurationMillis = lightAlarmDurationMillis.toInt()
    )

    LaunchedEffect(Unit) {
        /**
         * Example (with default (sunrise) settings)
         *
         * lightAlarmColors.size   = 6
         * lightAlarmDuration      = 30m
         * colorTransitionDuration = 30m / (6-1) = 6m (in millis) from color to color
         *
         *               lightAlarmStart                              actual alarmStart(sound)
         *                     |<                         30m                        >|
         *                     |                                                      |
         *                initialValue
         * lightAlarmA.C. => Black       Blue    LightBlue    Orange   Goldenrod    White
         *                     |          |          |          |          |          |
         *                     |<   6m   >|<   6m   >|<   6m   >|<   6m   >|<   6m   >|
         */
        val colorTransitionDuration =
                (lightAlarmDurationMillis / (lightAlarmColors.size - 1)).toInt()

        // drop(1) => first() already consumed as initial color
        lightAlarmColors.drop(1).forEach { nextColor ->
            lightAlarmAnimatedColor.animateTo(
                nextColor,
                animationSpec = tween(
                    durationMillis = colorTransitionDuration,
                    easing = LinearEasing
                )
            )
        }

        // Light alarm is finished => Unlock screen orientation => let user rotate again
        activity?.let {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED // -1
        }
        onFinished()
        // TODO: play alarm sound => actual alarm time is reached exactly here
    }
}
