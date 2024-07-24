package de.oljg.glac.feature_clock.ui.clock.utils

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import de.oljg.glac.core.utils.findActivity
import de.oljg.glac.core.utils.getScreenBrightness
import de.oljg.glac.core.utils.setScreenBrightness
import de.oljg.glac.feature_alarm.domain.model.Alarm

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
fun FadeSoundVolume(fadeDurationMillis: Int) {
    if(fadeDurationMillis == 0) return

    val context = LocalContext.current
    val audioManager by remember {
        mutableStateOf(context.getSystemService(AUDIO_SERVICE) as AudioManager)
    }

    val initialAlarmVolume = 0f
    val maxAlarmVolume by remember {
        mutableIntStateOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM))
    }

    var currentVolumeInt by remember {
        mutableIntStateOf(initialAlarmVolume.toInt())
    }

    val volume = remember { Animatable(initialAlarmVolume) }
    LaunchedEffect(Unit) {
        volume.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = fadeDurationMillis, easing = LinearEasing)
        ) {
            when {
                /**
                 * Set volume initially once to lowest level, to ensure the alarm sound starts
                 * playing with this low volume, otherwise alarm sound would be played with
                 * alarm volume set by user (most probably not lowest level!) until animation
                 * value reaches next integer, which would be not intended and odd^^ ...
                 */
                this.value == initialAlarmVolume -> {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, currentVolumeInt, 0)
                }
                /**
                 * Set volume only when next integer is reached, to prevent setting volume
                 * multiple times to the same value (e.g.: 1.1f => 1, 1.2f => 1, etc.)
                 */
                currentVolumeInt != (maxAlarmVolume * this.value).toInt() -> {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, currentVolumeInt, 0)
                    currentVolumeInt = (maxAlarmVolume * this.value).toInt()
                }
            }
        }
    }
}


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
        onFinished()
    }
}
