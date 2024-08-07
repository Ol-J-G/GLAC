package de.oljg.glac.feature_clock.ui.clock.portrait

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.oljg.glac.EmptyTestActivity
import de.oljg.glac.core.utils.TestTags
import de.oljg.glac.di.AppModule
import de.oljg.glac.feature_clock.ui.clock.components.SevenSegmentChar
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.test.isolated.DigitalClockPortraitLayoutIsolatedSevenSegment
import org.junit.Rule
import org.junit.Test

/**
 * Test digital clock portrait layout with:
 * - ClockCharType.SEVEN_SEGMENT clockChars and
 * - no dividers
 *
 * Note: Test tags (e.g. 'HOURS_TENS') are placed in a Column with a clockChar child each
 * (clockChar is a [SevenSegmentChar] in case of ClockCharType.SEVEN_SEGMENT;
 * and used in this test class)
 *
 * Debug: composeTestRule.onRoot(useUnmergedTree = false).printToLog("SEMANTICS_TREE")
 */
@HiltAndroidTest
@UninstallModules(AppModule::class)
class DigitalClockPortraitFormattedTimeTestWithoutDividerSevenSegment {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<EmptyTestActivity>()

    @Test
    fun time_format_HHMM() {

        // Given, we have the following formatted time: HHMM
        composeTestRule.setContent {
            DigitalClockPortraitLayoutIsolatedSevenSegment(
                currentTimeWithoutSeparators = "1234",
                dividerStyle = DividerStyle.NONE
            )
        }

        /**
         * (When we do nothing (this is just a layout test...))
         * Then, hours and minutes and must be displayed
         */
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_TENS) and hasAnyChild(hasContentDescription("1"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_ONES) and hasAnyChild(hasContentDescription("2"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_TENS) and hasAnyChild(hasContentDescription("3"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_ONES) and hasAnyChild(hasContentDescription("4"))
        ).assertIsDisplayed()

        // But, no seconds
        composeTestRule.onNode(hasTestTag(TestTags.SECONDS_TENS)).assertDoesNotExist()
        composeTestRule.onNode(hasTestTag(TestTags.SECONDS_ONES)).assertDoesNotExist()

        // And, no daytime marker
        composeTestRule.onNode(hasTestTag(TestTags.DAYTIME_MARKER_ANTE_OR_POST))
            .assertDoesNotExist()

        // And no dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.CHAR_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.COLON_DIVIDER)).assertCountEquals(0)
    }


    @Test
    fun time_format_HHMMSS() {

        // Given, we have the following formatted time: HHMMSS
        composeTestRule.setContent {
            DigitalClockPortraitLayoutIsolatedSevenSegment(
                currentTimeWithoutSeparators = "123456",
                dividerStyle = DividerStyle.NONE
            )
        }

        /**
         * (When we do nothing (this is just a layout test...))
         * Then, hours, minutes and seconds must be displayed
         */
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_TENS) and hasAnyChild(hasContentDescription("1"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_ONES) and hasAnyChild(hasContentDescription("2"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_TENS) and hasAnyChild(hasContentDescription("3"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_ONES) and hasAnyChild(hasContentDescription("4"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.SECONDS_TENS) and hasAnyChild(hasContentDescription("5"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.SECONDS_ONES) and hasAnyChild(hasContentDescription("6"))
        ).assertIsDisplayed()

        // But, no daytime marker
        composeTestRule.onNode(hasTestTag(TestTags.DAYTIME_MARKER_ANTE_OR_POST))
            .assertDoesNotExist()

        // And no dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.CHAR_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.COLON_DIVIDER)).assertCountEquals(0)
    }


    @Test
    fun time_format_HHMM_DAYTIME_MARKER() {

        // Given, we have the following formatted time: HH MM DAYTIME_MARKER
        composeTestRule.setContent {
            DigitalClockPortraitLayoutIsolatedSevenSegment(
                currentTimeWithoutSeparators = "1234A", // No meridiem in case of 7-seg
                dividerStyle = DividerStyle.NONE
            )
        }

        /**
         * (When we do nothing (this is just a layout test...))
         * Then, hours, minutes and daytime marker must be displayed
         */
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_TENS) and hasAnyChild(hasContentDescription("1"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_ONES) and hasAnyChild(hasContentDescription("2"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_TENS) and hasAnyChild(hasContentDescription("3"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_ONES) and hasAnyChild(hasContentDescription("4"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.DAYTIME_MARKER_ANTE_OR_POST) and hasAnyChild(hasContentDescription("A"))
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
            DigitalClockPortraitLayoutIsolatedSevenSegment(
                currentTimeWithoutSeparators = "123456A", // No meridiem in case of 7-seg
                dividerStyle = DividerStyle.NONE
            )
        }

        /**
         * (When we do nothing (this is just a layout test...))
         * Then, hours, minutes, seconds and daytime marker must be displayed
         */
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_TENS) and hasAnyChild(hasContentDescription("1"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_ONES) and hasAnyChild(hasContentDescription("2"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_TENS) and hasAnyChild(hasContentDescription("3"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.MINUTES_ONES) and hasAnyChild(hasContentDescription("4"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.SECONDS_TENS) and hasAnyChild(hasContentDescription("5"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.SECONDS_ONES) and hasAnyChild(hasContentDescription("6"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.DAYTIME_MARKER_ANTE_OR_POST) and hasAnyChild(hasContentDescription("A"))
        ).assertIsDisplayed()

        // And no dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.CHAR_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasTestTag(TestTags.COLON_DIVIDER)).assertCountEquals(0)
    }
}

