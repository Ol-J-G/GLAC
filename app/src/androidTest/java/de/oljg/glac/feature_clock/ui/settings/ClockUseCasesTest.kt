package de.oljg.glac.feature_clock.ui.settings

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.oljg.glac.core.utils.FontStyle
import de.oljg.glac.core.utils.FontWeight
import de.oljg.glac.di.AppModule
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.domain.use_case.ClockUseCases
import de.oljg.glac.feature_clock.ui.clock.utils.ClockCharType
import de.oljg.glac.feature_clock.ui.clock.utils.ClockPartsColors
import de.oljg.glac.feature_clock.ui.clock.utils.DividerLineEnd
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.feature_clock.ui.clock.utils.Segment
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentStyle
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentWeight
import de.oljg.glac.test.utils.TestCoroutineRule
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/**
 * Clock use cases integration test.
 *
 * Note that [de.oljg.glac.feature_clock.ui.ClockSettingsViewModel], which makes use of clock use
 * cases, will be covered in end-to-end and manual tests.
 *
 * Disadvantages:
 * - Longer execution time as an unit test!
 *
 * Advantages:
 * - No need to write (and also test?!?/maintain :>) a fake repository mock.
 * - It's close to reality (By using Hilt/TestAppModule, the injected clockUseCases are
 *   constructed with same 'infrastructure' as in real app, except datastore file, which is ofc
 *   wanted and necessary (similar as using in-memory DB in tests instead of real DB).
 *
 *  TODO_LATER: evaluate and maybe add edge case tests
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@UninstallModules(AppModule::class) // Better not to 'mix' with TestAppModule
class ClockUseCasesTest {
    /**
     * alarmUseCases will be executed in app's viewModelScope, but here in testing, a special test
     * dispatcher will be used to enable testing them in a test class.
     * See also runTest function below ...
     */
    @get:Rule(order = 0)
    var testCoroutineRule = TestCoroutineRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var clockUseCases: ClockUseCases

    @Before
    fun init() { hiltRule.inject() } // Get a 'fresh infrastructure' for every test case


    @Test
    fun clockSettingsDefaultValues() = runTest {
        val clockSettings = clockUseCases.getClockSettingsFlow.execute().first()
        assertThat(clockSettings).isEqualTo(ClockSettings())
    }


    @Test
    fun updateSimpleClockSettingsDefaultValues() = runTest {
        // Given, user A wants to update some simple clock default settings
        // When user A updates the following default clock settings
        clockUseCases.updateClockThemeName.execute("Testtheme")
        clockUseCases.updateOverrideSystemBrightness.execute(true)
        clockUseCases.updateClockBrightness.execute(.5f)
        clockUseCases.updateClockSettingsSectionPreviewIsExpanded.execute(true)
        clockUseCases.updateClockSettingsSectionThemeIsExpanded.execute(true)
        clockUseCases.updateClockSettingsSectionDisplayIsExpanded.execute(true)
        clockUseCases.updateClockSettingsSectionClockCharIsExpanded.execute(true)
        clockUseCases.updateClockSettingsSectionDividerIsExpanded.execute(true)
        clockUseCases.updateClockSettingsSectionColorsIsExpanded.execute(true)
        clockUseCases.updateClockSettingsSectionBrightnessIsExpanded.execute(true)
        clockUseCases.updateClockSettingsColumnScrollPosition.execute(1)
        clockUseCases.updateClockSettingsStartColumnScrollPosition.execute(2)
        clockUseCases.updateClockSettingsEndColumnScrollPosition.execute(3)

        // Then, user A expects, that those settings are persited
        val clockSettings = clockUseCases.getClockSettingsFlow.execute().first()
        assertThat(clockSettings.clockThemeName).isEqualTo("Testtheme")
        assertThat(clockSettings.overrideSystemBrightness).isEqualTo(true)
        assertThat(clockSettings.clockBrightness).isEqualTo(.5f)
        assertThat(clockSettings.clockSettingsSectionPreviewIsExpanded).isTrue()
        assertThat(clockSettings.clockSettingsSectionThemeIsExpanded).isTrue()
        assertThat(clockSettings.clockSettingsSectionDisplayIsExpanded).isTrue()
        assertThat(clockSettings.clockSettingsSectionClockCharIsExpanded).isTrue()
        assertThat(clockSettings.clockSettingsSectionDividerIsExpanded).isTrue()
        assertThat(clockSettings.clockSettingsSectionColorsIsExpanded).isTrue()
        assertThat(clockSettings.clockSettingsSectionBrigntnessIsExpanded).isTrue()
        assertThat(clockSettings.clockSettingsColumnScrollPosition).isEqualTo(1)
        assertThat(clockSettings.clockSettingsStartColumnScrollPosition).isEqualTo(2)
        assertThat(clockSettings.clockSettingsEndColumnScrollPosition).isEqualTo(3)
    }


    @Test
    fun addTheme() = runTest {
        // Given, user A wants to add the following clock theme
        val testClockThemName = "userATheme"
        val testClockTheme = buildTestClockTheme()

        // And user A has not been added any other clock theme before
        assertThat(clockUseCases.getClockSettingsFlow.execute().first().themes)
            .hasSize(1) // Default clock theme is always part of default clock settings

        // When user A adds 'testClockTheme'
        clockUseCases.updateThemes.execute(
            clockThemeName = testClockThemName,
            clockTheme = testClockTheme
        )

        // Then, user A expects, that 'testClockTheme' with name 'testClockThemName' has been added
        val themes = clockUseCases.getClockSettingsFlow.execute().first().themes
        assertThat(themes).hasSize(2)
        assertThat(themes[testClockThemName]).isEqualTo(testClockTheme)
    }


    @Test
    fun removeTheme() = runTest {
        // Given, user A has added the following clock theme
        val testClockThemName = "userATheme"
        val testClockTheme = buildTestClockTheme()
        clockUseCases.updateThemes.execute(
            clockThemeName = testClockThemName,
            clockTheme = testClockTheme
        )

        // When user A removes the theme
        clockUseCases.removeTheme.execute(testClockThemName)

        /**
         * Then, user A expects, that 'testClockTheme' with name 'testClockThemeName' has been
         * removed
         */
        val themes = clockUseCases.getClockSettingsFlow.execute().first().themes
        assertThat(themes).hasSize(1) // Default still exists ofc
        assertThat(themes[testClockThemName]).isEqualTo(null)
    }


    // This is by far the most used case! => See ClockSettingsScreen ...
    @Test
    fun updateTheme() = runTest {
        /**
         * Given, user A has added the following clock theme a while ago and wants to update some
         * values
         */
        val testClockThemName = "userATheme"
        val testClockTheme = buildTestClockTheme()
        clockUseCases.updateThemes.execute(
            clockThemeName = testClockThemName,
            clockTheme = testClockTheme
        )

        // When user A wants to see seconds and all 2's to be yellow
        val changedTestClockTheme = testClockTheme.copy(
            showSeconds = true,
            charColors = buildCharColors().mutate { mutableCharColors ->
                mutableCharColors['2'] = Color.Yellow
            }.toPersistentMap()
        )
        clockUseCases.updateThemes.execute(
            clockThemeName = testClockThemName,
            clockTheme = changedTestClockTheme
        )

        // Then, user A expects the changes to be persited
        val themes = clockUseCases.getClockSettingsFlow.execute().first().themes
        assertThat(themes).hasSize(2)
        assertThat(themes[testClockThemName]).isEqualTo(changedTestClockTheme)
    }


    // Build a test clock theme with no default values each (fully customized)
    private fun buildTestClockTheme() = ClockTheme(
        showSeconds = false,
        showDaytimeMarker = true,

        clockCharType = ClockCharType.SEVEN_SEGMENT,
        /**
         * To cover every properties, also set font stuff, even if it might seem illogical,
         * to set these, when ClockCharType.SEVEN_SEGMENT is set...
         */
        fontName = "Exo_2_SemiBoldItalic.otf",
        fontWeight = FontWeight.SEMI_BOLD, // In reality, it will be recognized by (asset) fontName
        fontStyle = FontStyle.ITALIC, // In reality, it will be recognized by (asset) fontName

        sevenSegmentStyle = SevenSegmentStyle.OUTLINE_REVERSE_ITALIC,
        sevenSegmentWeight = SevenSegmentWeight.MEDIUM,
        sevenSegmentOutlineSize = 7f,
        drawOffSegments = false,

        digitSizeFactor = .7f,
        daytimeMarkerSizeFactor = .5f,

        dividerStyle = DividerStyle.DASHED_LINE,
        dividerThickness = 5,
        dividerLengthPercentage = .55f,
        dividerDashCount = 3,
        dividerLineEnd = DividerLineEnd.ANGULAR,
        dividerRotateAngle = 11f,

        /**
         * The following divider stuff is also just set to cover every property ...
         * (they would have no effect on DividerStyle.DASHED_LINE!)
         */
        dividerDashDottedPartCount = 16,
        colonFirstCirclePosition = .2f,
        colonSecondCirclePosition = .3f,
        hoursMinutesDividerChar = ',',
        minutesSecondsDividerChar = '.',
        daytimeMarkerDividerChar = '-',

        charColor = Color.Red,
        dividerColor = Color.Gray,
        backgroundColor = Color.Yellow,

        useColorsPerChar = true,
        charColors = buildCharColors(),

        useColorsPerClockPart = true,
        clockPartsColors = buildClockPartColors(),

        useSegmentColors = true,
        segmentColors = buildSegmentColors()
    )

    private fun buildCharColors() = buildMap {
        put('0', Color.Yellow)
        put('1', Color.Red)
        put('2', Color.Black)
        put('3', Color.Gray)
        put('4', Color.Blue)
        put('5', Color.Cyan)
        put('6', Color.Magenta)
        put('7', Color.DarkGray)
        put('8', Color.Green)
        put('9', Color.White)
        put('A', Color.LightGray)
        put('P', Color.Transparent)
        put('M', Color.Yellow)
    }.toPersistentMap()

    private fun buildClockPartColors() = ClockPartsColors(
        hours = ClockPartsColors.DigitPairColor(ones = Color.Yellow, tens = Color.LightGray),
        minutes = ClockPartsColors.DigitPairColor(ones = Color.White, tens = Color.Green),
        seconds = ClockPartsColors.DigitPairColor(ones = Color.Magenta, tens = Color.DarkGray),
        daytimeMarker = ClockPartsColors.DaytimeMarkerColor(
            anteOrPost = Color.Blue, meridiem = Color.Black
        ),
        dividers = ClockPartsColors.DividerColor(
            daytimeMarker = Color.Red,
            hoursMinutes = Color.Gray,
            minutesSeconds = Color.Cyan
        )
    )

    private fun buildSegmentColors() = buildMap {
        put(Segment.TOP, Color.Yellow)
        put(Segment.CENTER, Color.Gray)
        put(Segment.BOTTOM, Color.Green)
        put(Segment.TOP_LEFT, Color.White)
        put(Segment.TOP_RIGHT, Color.Black)
        put(Segment.BOTTOM_LEFT, Color.Red)
        put(Segment.BOTTOM_RIGHT, Color.DarkGray)
    }.toPersistentMap()
}
