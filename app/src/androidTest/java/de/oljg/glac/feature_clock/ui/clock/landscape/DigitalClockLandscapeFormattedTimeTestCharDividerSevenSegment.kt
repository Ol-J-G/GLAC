package de.oljg.glac.feature_clock.ui.clock.landscape

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import de.oljg.glac.core.util.TestTags
import de.oljg.glac.feature_clock.ui.clock.components.SevenSegmentChar
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.test.isolated.DigitalClockLandscapeLayoutIsolatedSevenSegment
import org.junit.Rule
import org.junit.Test

/**
 * Test, that digital clock landscape layout with:
 * - ClockCharType.SEVEN_SEGMENT clockChars and
 * - CHAR dividers
 * must fall back to LINE dividers
 *
 * Note: Test tags (e.g. 'HOURS_TENS') are placed in a Column with a clockChar child each
 * (clockChar is a [SevenSegmentChar] in case of ClockCharType.SEVEN_SEGMENT;
 * and used in this test class)
 *
 * Debug: composeTestRule.onRoot(useUnmergedTree = false).printToLog("SEMANTICS_TREE")
 */
class DigitalClockLandscapeFormattedTimeTestCharDividerSevenSegment {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun time_format_HH_MM() {

        // Given, we have the following formatted time: HH:MM
        composeTestRule.setContent {
            DigitalClockLandscapeLayoutIsolatedSevenSegment(
                currentTimeFormatted = "12:34",
                dividerStyle = DividerStyle.CHAR
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

        // And 1 line divider
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(1)
    }


    @Test
    fun time_format_HH_MM_SS() {

        // Given, we have the following formatted time: HH:MM:SS
        composeTestRule.setContent {
            DigitalClockLandscapeLayoutIsolatedSevenSegment(
                currentTimeFormatted = "12:34:56",
                dividerStyle = DividerStyle.CHAR
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

        // And 2 line dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(2)
    }


    @Test
    fun time_format_HH_MM_DAYTIME_MARKER() {

        // Given, we have the following formatted time: HH:MM:DAYTIME_MARKER
        composeTestRule.setContent {
            DigitalClockLandscapeLayoutIsolatedSevenSegment(
                currentTimeFormatted = "12:34:A",
                dividerStyle = DividerStyle.CHAR
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

        // And 2 line dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(2)
    }


    @Test
    fun time_format_HH_MM_SS_DAYTIME_MARKER() {

        // Given, we have the following formatted time: HH:MM:SS:DAYTIME_MARKER
        composeTestRule.setContent {
            DigitalClockLandscapeLayoutIsolatedSevenSegment(
                currentTimeFormatted = "12:34:56:A",
                dividerStyle = DividerStyle.CHAR
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

        // And 3 line dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(3)
    }
}

