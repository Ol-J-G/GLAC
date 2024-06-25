package de.oljg.glac.feature_clock.ui.clock.landscape

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import de.oljg.glac.core.util.TestTags
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.test.isolated.DigitalClockLandscapeLayoutIsolatedFont
import org.junit.Rule
import org.junit.Test

/**
 * Test digital clock landscape layout with LINE dividers.
 *
 * Note: Test tags (e.g. 'HOURS_TENS') are placed in a Column with a clockChar child each
 * (clockChar is a Text-composable in case of ClockCharType.FONT; and used in this test class)
 *
 * Debug: composeTestRule.onRoot(useUnmergedTree = false).printToLog("SEMANTICS_TREE")
 */
class DigitalClockLandscapeFormattedTimeTestLineDivider {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun time_format_HH_MM() {

        // Given, we have the following formatted time: HH:MM
        composeTestRule.setContent {
            DigitalClockLandscapeLayoutIsolatedFont(
                currentTimeFormatted = "12:34",
                dividerStyle = DividerStyle.LINE
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

        // And 1 line divider
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(1)
    }


    @Test
    fun time_format_HH_MM_SS() {

        // Given, we have the following formatted time: HH:MM:SS
        composeTestRule.setContent {
            DigitalClockLandscapeLayoutIsolatedFont(
                currentTimeFormatted = "12:34:56",
                dividerStyle = DividerStyle.LINE
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

        // And 2 line dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(2)
    }


    @Test
    fun time_format_HH_MM_DAYTIME_MARKER() {

        // Given, we have the following formatted time: HH:MM:DAYTIME_MARKER
        composeTestRule.setContent {
            DigitalClockLandscapeLayoutIsolatedFont(
                currentTimeFormatted = "12:34:AM",
                dividerStyle = DividerStyle.LINE
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

        // And 2 line dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(2)
    }


    @Test
    fun time_format_HH_MM_SS_DAYTIME_MARKER() {

        // Given, we have the following formatted time: HH:MM:SS:DAYTIME_MARKER
        composeTestRule.setContent {
            DigitalClockLandscapeLayoutIsolatedFont(
                currentTimeFormatted = "12:34:56:AM",
                dividerStyle = DividerStyle.LINE
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

        // And 3 line dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.LINE_DIVIDER)).assertCountEquals(3)
    }
}

