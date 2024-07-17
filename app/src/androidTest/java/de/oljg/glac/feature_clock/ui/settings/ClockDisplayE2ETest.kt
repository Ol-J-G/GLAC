package de.oljg.glac.feature_clock.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.oljg.glac.MainActivity
import de.oljg.glac.core.util.TestTags.CLOCK_PREVIEW_EXPANDABLE_SECTION
import de.oljg.glac.core.util.TestTags.CLOCK_SETTINGS_DISPLAY_EXPANDABLE_SECTION
import de.oljg.glac.core.util.TestTags.CLOCK_SETTINGS_SCREEN
import de.oljg.glac.core.util.TestTags.DAYTIME_MARKER_ANTE_OR_POST
import de.oljg.glac.core.util.TestTags.DAYTIME_MARKER_MERIDIEM
import de.oljg.glac.core.util.TestTags.DIGITAL_CLOCK
import de.oljg.glac.core.util.TestTags.HOURS_ONES
import de.oljg.glac.core.util.TestTags.HOURS_TENS
import de.oljg.glac.core.util.TestTags.MINUTES_ONES
import de.oljg.glac.core.util.TestTags.MINUTES_TENS
import de.oljg.glac.core.util.TestTags.SECONDS_ONES
import de.oljg.glac.core.util.TestTags.SECONDS_TENS
import de.oljg.glac.core.util.TestTags.SETTINGS_TAB
import de.oljg.glac.core.util.TestTags.SHOW_SECONDS_SWITCH
import de.oljg.glac.di.AppModule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * End-To-End tests for clock display settings (currently show/hide digital clock's seconds
 * and/or daytime marker)
 *
 * This is just an exemplary E2E test to show, that I'm able to write such tests. I could
 * add a ton of E2E tests for this project, but this is covered by a lot of manual tests
 * I already did and will do in the future, because I'm using GLAC myself day by day! :)
 *
 * Note
 * Unfortunately, it's not possible to use string resources here,
 * so, in order I also can't use content description => must use test tags! :/ (and "pollute"
 * non-test source code with them...)
 *
 * Debug:
 * composeRule.onRoot(useUnmergedTree = true).printToLog("SEMANTICS_TREE")
 */
@HiltAndroidTest
@UninstallModules(AppModule::class) // Better not to 'mix' with TestAppModule
class ClockDisplayE2ETest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() { hiltRule.inject() }

    /**
     * Given, user A wants to disable clock's seconds
     */
    @Test
    fun disableSeconds() {
        // When user A navigates to clock settings
        composeRule.onNodeWithTag(DIGITAL_CLOCK).performClick() // fullscreen to not-fullscreen
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(SETTINGS_TAB).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(CLOCK_SETTINGS_SCREEN).assertIsDisplayed()

        // And user A expands clock preview
        composeRule.onNodeWithTag(CLOCK_PREVIEW_EXPANDABLE_SECTION).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(DIGITAL_CLOCK).assertIsDisplayed() // as clock preview

        // And user A sees clock's default settings (default is 'HHMMSS' (no daytime marker))
        composeRule.onNode(
            hasTestTag(DIGITAL_CLOCK)
                    and hasAnyDescendant(hasTestTag(HOURS_TENS))
                    and hasAnyDescendant(hasTestTag(HOURS_ONES))
                    and hasAnyDescendant(hasTestTag(MINUTES_TENS))
                    and hasAnyDescendant(hasTestTag(MINUTES_ONES))
                    and hasAnyDescendant(hasTestTag(SECONDS_TENS))
                    and hasAnyDescendant(hasTestTag(SECONDS_ONES)),
            useUnmergedTree = true
        ).assertIsDisplayed()
        composeRule.onNode(
            hasTestTag(DIGITAL_CLOCK)
                    and hasAnyDescendant(hasTestTag(DAYTIME_MARKER_ANTE_OR_POST))
                    and hasAnyDescendant(hasTestTag(DAYTIME_MARKER_MERIDIEM)),
            useUnmergedTree = true
        ).assertIsNotDisplayed()

        // And user A expands clock settings display section
        composeRule.onNodeWithTag(CLOCK_SETTINGS_DISPLAY_EXPANDABLE_SECTION).performClick()
        composeRule.waitForIdle()

        // And user A disables seconds
        composeRule.onNodeWithTag(SHOW_SECONDS_SWITCH).performClick()
        composeRule.waitForIdle()

        // Then, seconds must be disabled and user A must see exactly this in clock preview
        composeRule.onNode(
            hasTestTag(DIGITAL_CLOCK)
                    and hasAnyDescendant(hasTestTag(HOURS_TENS))
                    and hasAnyDescendant(hasTestTag(HOURS_ONES))
                    and hasAnyDescendant(hasTestTag(MINUTES_TENS))
                    and hasAnyDescendant(hasTestTag(MINUTES_ONES)),
            useUnmergedTree = true
        ).assertIsDisplayed()
        composeRule.onNode(
            hasTestTag(DIGITAL_CLOCK)
                    and hasAnyDescendant(hasTestTag(SECONDS_TENS))
                    and hasAnyDescendant(hasTestTag(SECONDS_ONES))
                    and hasAnyDescendant(hasTestTag(DAYTIME_MARKER_ANTE_OR_POST))
                    and hasAnyDescendant(hasTestTag(DAYTIME_MARKER_MERIDIEM)),
            useUnmergedTree = true
        ).assertIsNotDisplayed()
    }
}
