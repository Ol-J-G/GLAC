package de.oljg.glac.settings.clock.ui.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SettingsUtilsTest {
    @Test
    fun testFormatTwoDecimalPlaces_lengthOf3() {
        val testFloat = 0.9f
        assertThat(testFloat.formatTwoDecimalPlaces()).isEqualTo("0.90")
    }

    @Test
    fun testFormatTwoDecimalPlaces_lengthGreaterThan3_tens() {
        val testFloat = 0.45678f
        assertThat(testFloat.formatTwoDecimalPlaces()).isEqualTo("0.45")
    }

    @Test
    fun testFormatTwoDecimalPlaces_lengthGreaterThan3_ones() {
        val testFloat = 0.07543f
        assertThat(testFloat.formatTwoDecimalPlaces()).isEqualTo("0.07")
    }

    @Test
    fun testFormatTwoDecimalPlaces_zero() {
        val testFloat = 0f // is internally 0.0
        assertThat(testFloat.formatTwoDecimalPlaces()).isEqualTo("0.00")
    }

    @Test
    fun testFormatTwoDecimalPlaces_one() {
        val testFloat = 1f // is internally 1.0
        assertThat(testFloat.formatTwoDecimalPlaces()).isEqualTo("1.00")
    }
}
