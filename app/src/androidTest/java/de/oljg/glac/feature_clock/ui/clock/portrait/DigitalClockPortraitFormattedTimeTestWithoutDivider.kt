package de.oljg.glac.feature_clock.ui.clock.portrait

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.oljg.glac.EmptyTestActivity
import de.oljg.glac.core.utils.TestTags
import de.oljg.glac.di.AppModule
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.test.isolated.DigitalClockPortraitLayoutIsolatedFont
import org.junit.Rule
import org.junit.Test

/**
 * Test digital clock portrait layout without dividers.
 *
 * Note: Test tags (e.g. 'HOURS_TENS') are placed in a Column with a clockChar child each
 * (clockChar is a Text-composable in case of ClockCharType.FONT; and used in this test class)
 *
 * Debug: composeTestRule.onRoot(useUnmergedTree = false).printToLog("SEMANTICS_TREE")
 */
@HiltAndroidTest
@UninstallModules(AppModule::class)
class DigitalClockPortraitFormattedTimeTestWithoutDivider {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<EmptyTestActivity>()

    @Test
    fun time_format_HHMM() {

        // Given, we have the following formatted time: HHMM
        composeTestRule.setContent {
            DigitalClockPortraitLayoutIsolatedFont(
                currentTimeWithoutSeparators = "1234",
                dividerStyle = DividerStyle.NONE
            )
        }

        /**
         * (When we do nothing (this is just a layout test...))
         * Then, hours and minutes and must be displayed
         */
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_TENS) and hasAnyChild(hasText("1"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_ONES) and hasAnyChild(hasText("2"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_TENS) and hasAnyChild(hasText("3"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_ONES) and hasAnyChild(hasText("4"))
        ).assertIsDisplayed()

        // But, no seconds
        composeTestRule.onNode(hasTestTag(TestTags.SECONDS_TENS)).assertDoesNotExist()
        composeTestRule.onNode(hasTestTag(TestTags.SECONDS_ONES)).assertDoesNotExist()

        // And, no daytime marker
        composeTestRule.onNode(hasTestTag(TestTags.DAYTIME_MARKER_ANTE_OR_POST))
            .assertDoesNotExist()
        composeTestRule.onNode(hasTestTag(TestTags.DAYTIME_MARKER_MERIDIEM)).assertDoesNotExist()

        // And no dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.CHAR_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.COLON_DIVIDER)).assertCountEquals(0)
    }


    @Test
    fun time_format_HHMMSS() {

        // Given, we have the following formatted time: HHMMSS
        composeTestRule.setContent {
            DigitalClockPortraitLayoutIsolatedFont(
                currentTimeWithoutSeparators = "123456",
                dividerStyle = DividerStyle.NONE
            )
        }

        /**
         * (When we do nothing (this is just a layout test...))
         * Then, hours, minutes and seconds must be displayed
         */
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_TENS) and hasAnyChild(hasText("1"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_ONES) and hasAnyChild(hasText("2"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_TENS) and hasAnyChild(hasText("3"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_ONES) and hasAnyChild(hasText("4"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.SECONDS_TENS) and hasAnyChild(hasText("5"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.SECONDS_ONES) and hasAnyChild(hasText("6"))
        ).assertIsDisplayed()

        // But, no daytime marker
        composeTestRule.onNode(hasTestTag(TestTags.DAYTIME_MARKER_ANTE_OR_POST))
            .assertDoesNotExist()
        composeTestRule.onNode(hasTestTag(TestTags.DAYTIME_MARKER_MERIDIEM)).assertDoesNotExist()

        // And no dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.CHAR_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.COLON_DIVIDER)).assertCountEquals(0)
    }


    @Test
    fun time_format_HHMM_DAYTIME_MARKER() {

        // Given, we have the following formatted time: HH MM DAYTIME_MARKER
        composeTestRule.setContent {
            DigitalClockPortraitLayoutIsolatedFont(
                currentTimeWithoutSeparators = "1234AM",
                dividerStyle = DividerStyle.NONE
            )
        }

        /**
         * (When we do nothing (this is just a layout test...))
         * Then, hours, minutes and daytime marker must be displayed
         */
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_TENS) and hasAnyChild(hasText("1"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_ONES) and hasAnyChild(hasText("2"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_TENS) and hasAnyChild(hasText("3"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_ONES) and hasAnyChild(hasText("4"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.DAYTIME_MARKER_ANTE_OR_POST) and hasAnyChild(hasText("A"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.DAYTIME_MARKER_MERIDIEM) and hasAnyChild(hasText("M"))
        ).assertIsDisplayed()

        // But, no seconds
        composeTestRule.onNode(hasTestTag(TestTags.SECONDS_TENS)).assertDoesNotExist()
        composeTestRule.onNode(hasTestTag(TestTags.SECONDS_ONES)).assertDoesNotExist()

        // And no dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.CHAR_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.COLON_DIVIDER)).assertCountEquals(0)
    }


    @Test
    fun time_format_HHMMSS_DAYTIME_MARKER() {

        // Given, we have the following formatted time: HH MM SS DAYTIME_MARKER
        composeTestRule.setContent {
            DigitalClockPortraitLayoutIsolatedFont(
                currentTimeWithoutSeparators = "123456AM",
                dividerStyle = DividerStyle.NONE
            )
        }

        /**
         * (When we do nothing (this is just a layout test...))
         * Then, hours, minutes, seconds and daytime marker must be displayed
         */
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_TENS) and hasAnyChild(hasText("1"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_ONES) and hasAnyChild(hasText("2"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_TENS) and hasAnyChild(hasText("3"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_ONES) and hasAnyChild(hasText("4"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.SECONDS_TENS) and hasAnyChild(hasText("5"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.SECONDS_ONES) and hasAnyChild(hasText("6"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.DAYTIME_MARKER_ANTE_OR_POST) and hasAnyChild(hasText("A"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.DAYTIME_MARKER_MERIDIEM) and hasAnyChild(hasText("M"))
        ).assertIsDisplayed()

        // And no dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.CHAR_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.COLON_DIVIDER)).assertCountEquals(0)
    }
}

