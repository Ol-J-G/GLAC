package de.oljg.glac.feature_clock.ui.settings.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class FloatFormatTest(
    private val input: Float,
    private val places: Int,
    private val percentage: Boolean,
    private val expectedOutput: String
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(

            // input, places, percentage, expectedOutput
            arrayOf(0f, 0, false, "0"),
            arrayOf(1f, 0, false, "1"),
            arrayOf(0.45f, 0, false, "0"),
            arrayOf(1.45f, 0, false, "1"),
            arrayOf(12.45f, 0, false, "12"),
            arrayOf(-12.45f, 0, false, "-12"),
            arrayOf(0f, 1, false, "0.0"),
            arrayOf(1f, 1, false, "1.0"),
            arrayOf(0.45f, 1, false, "0.4"),
            arrayOf(1.45f, 1, false, "1.4"),
            arrayOf(12.45f, 1, false, "12.4"),
            arrayOf(-12.45f, 1, false, "-12.5"), // Rounds down, towards 0 (floor)
            arrayOf(0f, 2, false, "0.0"),
            arrayOf(1f, 2, false, "1.0"),
            arrayOf(0.45f, 2, false, "0.45"),
            arrayOf(1.45f, 2, false, "1.45"),
            arrayOf(12.456f, 2, false, "12.45"),
            arrayOf(-12.456f, 2, false, "-12.46"), // Rounds down, towards 0 (floor)
            arrayOf(0f, 2, true, "0.00"),
            arrayOf(1f, 2, true, "1.00"),
            arrayOf(0.9f, 2, true, "0.90"),
            arrayOf(0.45f, 2, true, "0.45"),
            arrayOf(0f, 3, false, "0.0"),
            arrayOf(1f, 3, false, "1.0"),
            arrayOf(0.4567f, 3, false, "0.456"),
            arrayOf(1.4567f, 3, false, "1.456"),
            arrayOf(12.4567f, 3, false, "12.456"),
            arrayOf(-123.4567f, 3, false, "-123.457"), // Rounds down, towards 0 (floor)
            // stopping here => rarely more than 3 decimal places needed ...
        )
    }

    @Test
    fun `Formatting Floats works as expected`() {
        assertThat(input.format(places = places, percentage = percentage))
            .isEqualTo(expectedOutput)
    }
}
