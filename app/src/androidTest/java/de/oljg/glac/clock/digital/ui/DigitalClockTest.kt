package de.oljg.glac.clock.digital.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.test.platform.app.InstrumentationRegistry
import de.oljg.glac.R
import de.oljg.glac.ui.theme.GLACTheme
import org.junit.Rule
import org.junit.Test

class DigitalClockTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    //TODO: need to start emulator in right orientation before starting the test => how to solve this, different run configprofiles?
    @Test
    fun digitalClock_Portrait_Font_isVisible() {
        composeTestRule.setContent {
            GLACTheme {
                DigitalClock(currentTimeFormatted = "12:34:56") { char, finalFontSize, clockCharColor, _, _ ->
                    Text(
                        text = char.toString(),
                        fontSize = finalFontSize,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                        color = clockCharColor,
                    )
                }
            }
        }

        composeTestRule.onRoot().printToLog("SEMANTICS_TREE")

        composeTestRule
            .onNodeWithContentDescription(stringRes(R.string.digital_clock_in_portrait_layout))
            .assertExists()

        composeTestRule
            .onNodeWithContentDescription(stringRes(R.string.digital_clock_in_portrait_layout))
            .assertTextEquals("MM", "1", "2", "3", "4", "5", "6")
    }
}

//TODO: introduce "TestBase" and relocate below utils to it
private fun stringRes(@StringRes resId: Int): String {
    return LocalContext.current.getString(resId)
}

object LocalContext {
    val current: Context = InstrumentationRegistry.getInstrumentation().targetContext
}