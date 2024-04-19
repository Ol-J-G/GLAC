package de.oljg.glac.clock.digital.ui

import android.view.Surface.ROTATION_0
import android.view.Surface.ROTATION_90
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.platform.app.InstrumentationRegistry
import de.oljg.glac.MainActivity
import de.oljg.glac.core.util.TestTags
import de.oljg.glac.test.util.UITestBase
import org.junit.Rule
import org.junit.Test


/**
 * Digital clock must be displayed in correct layout, depending on test device's orientation,
 * as well as font measurement has to be not existent anymore after measurement is completed.
 *
 * NOTE: setRotation() just works with activity => createAndroidComposeRule<>(), not with
 * tests in isolation (createComposeRule())!
 */
class DigitalClockTest: UITestBase() {

    @get:Rule
    val androidComposeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun digitalClock_Portrait_isDisplayed() {
        /**
         * Given, we start the app and set orientation to portrait.
         * (If not already in portrait orientation => rotate (e.g. my test device starts in
         * portrait orientation by default, so, just in case I forget to rotate it back to portrait,
         * before I start this test, let's say, after some manual testing in landscape orientation)
         */
        InstrumentationRegistry.getInstrumentation().uiAutomation.setRotation(ROTATION_0)

        /**
         * When we do nothing (App starts by default with fullscreen digital clock)
         * Then, the digital clock must be displayed in portrait orientation ...
         */
        androidComposeTestRule
            .onNodeWithTag(TestTags.DIGITAL_CLOCK_PORTRAIT_LAYOUT, useUnmergedTree = true)
            .assertIsDisplayed()

        /**
         * And, Text for font measurement must exist just as long as font size measuremnt
         * is ongoing, but not afterwards, then just the clock chars must be displayed at all.
         * Note: ClockCharType.FONT is default
         */
        androidComposeTestRule
            .onNodeWithTag(TestTags.FONT_MEASUREMENT, useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun digitalClock_Landscape_isDisplayed() {
        /**
         * Given, we start the app set test device to landscape orientation
         */
        InstrumentationRegistry.getInstrumentation().uiAutomation.setRotation(ROTATION_90)

        /**
         * When we do nothing (App starts by default with fullscreen digital clock)
         * Then, the digital clock must be displayed in landscape orientation ...
         */
        androidComposeTestRule
            .onNodeWithTag(TestTags.DIGITAL_CLOCK_LANDSCAPE_LAYOUT, useUnmergedTree = true)
            .assertIsDisplayed()

        /**
         * And, Text for font measurement must exist just as long as font size measuremnt
         * is ongoing, but not afterwards, then just the clock chars must be displayed at all.
         * Note: ClockCharType.FONT is default
         */
        androidComposeTestRule
            .onNodeWithTag(TestTags.FONT_MEASUREMENT, useUnmergedTree = true)
            .assertDoesNotExist()

        // Reset rotation
        InstrumentationRegistry.getInstrumentation().uiAutomation.setRotation(ROTATION_0)
    }
}


