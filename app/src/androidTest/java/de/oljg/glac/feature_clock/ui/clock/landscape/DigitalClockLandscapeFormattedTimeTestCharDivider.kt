package de.oljg.glac.feature_clock.ui.clock.landscape

import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.oljg.glac.EmptyTestActivity
import de.oljg.glac.core.utils.TestTags
import de.oljg.glac.di.AppModule
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_HOURS_MINUTES_DIVIDER_CHAR
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.test.isolated.DigitalClockLandscapeLayoutIsolatedFont
import org.junit.Rule
import org.junit.Test


/**
 * Test digital clock landscape layout with default CHAR divider.
 *
 * Note: Test tags (e.g. 'HOURS_TENS') are placed in a Column with a clockChar child each
 * (clockChar is a Text-composable in case of ClockCharType.FONT; and used in this test class)
 *
 * Debug: composeTestRule.onRoot(useUnmergedTree = false).printToLog("SEMANTICS_TREE")
 */
@HiltAndroidTest
@UninstallModules(AppModule::class)
class DigitalClockLandscapeFormattedTimeTestCharDivider {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<EmptyTestActivity>()

    @Test
    fun time_format_HH_MM() {

        // Given, we have the following formatted time: HH:MM
        composeTestRule.setContent {
            DigitalClockLandscapeLayoutIsolatedFont(
                currentTimeFormatted = "12:34",
                dividerStyle = DividerStyle.CHAR
            )
        }


        /**
         * (When we do nothing (this is just a layout test...))
         * Then, hours, minutes and 1 char divider must be displayed
         */
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_TENS) and hasAnyChild(hasText("1"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.HOURS_ONES) and hasAnyChild(hasText("2"))
        ).assertIsDisplayed()
        composeTestRule.onNode(
            hasTestTag(TestTags.CHAR_DIVIDER) and
                    hasAnyChild(hasText(DEFAULT_HOURS_MINUTES_DIVIDER_CHAR.toString()))
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
    }


    @Test
    fun time_format_HH_MM_SS() {

        // Given, we have the following formatted time: HH:MM:SS
        composeTestRule.setContent {
            DigitalClockLandscapeLayoutIsolatedFont(
                currentTimeFormatted = "12:34:56",
                dividerStyle = DividerStyle.CHAR
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

        // And, 2 char dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.CHAR_DIVIDER)
        ).assertAll(hasAnyChild(hasTextExactly(":", ":")))
    }


    @Test
    fun time_format_HH_MM_DAYTIME_MARKER() {

        // Given, we have the following formatted time: HH:MM:DAYTIME_MARKER
        composeTestRule.setContent {
            DigitalClockLandscapeLayoutIsolatedFont(
                currentTimeFormatted = "12:34:AM",
                dividerStyle = DividerStyle.CHAR
            )
        }

        composeTestRule.onRoot(useUnmergedTree = false).printToLog("SEMANTICS_TREE")

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

        // And, 2 char dividers
        composeTestRule.onAllNodes(hasTestTag(TestTags.CHAR_DIVIDER)
        ).assertAll(hasAnyChild(hasTextExactly(":", ":")))
    }


    @Test
    fun time_format_HH_MM_SS_DAYTIME_MARKER() {

        // Given, we have the following formatted time: HH:MM:SS:DAYTIME_MARKER
        composeTestRule.setContent {
            DigitalClockLandscapeLayoutIsolatedFont(
                currentTimeFormatted = "12:34:56:AM",
                dividerStyle = DividerStyle.CHAR
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

        // And, 3 char dividers
        composeTestRule.onAllNodes(
            hasTestTag(TestTags.CHAR_DIVIDER)
        ).assertAll(hasAnyChild(hasTextExactly(":", ":", ":")))
    }
}

