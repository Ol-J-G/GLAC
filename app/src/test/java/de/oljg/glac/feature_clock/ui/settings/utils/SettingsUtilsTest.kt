package de.oljg.glac.feature_clock.ui.settings.utils

import com.google.common.truth.Truth.assertThat
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.FLOAT_PERCENTAGE_BETWEEN_ZERO_ONE
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.PERCENTAGE_ONLY_2_PLACES
import de.oljg.glac.utils.UnitTestDefaults.FAILED_REQUIREMENT
import org.junit.Assert.assertThrows
import org.junit.Test

class SettingsUtilsTest {

    // negative test
    @Test
    fun `Format float percentage, float with places != 2 must throw exception`() {
        val testFloat = 0.4567f
        val e = assertThrows(IllegalArgumentException::class.java) {
            testFloat.format(places = 0, percentage = true)
        }
        assertThat(e).hasMessageThat().isEqualTo(PERCENTAGE_ONLY_2_PLACES)
    }

    // negative test
    @Test
    fun `Format float percentage, float input has to be less than 1f`() {
        val testFloat = 2.4567f
        val e = assertThrows(IllegalArgumentException::class.java) {
            testFloat.format(places = 2, percentage = true)
        }
        assertThat(e).hasMessageThat().isEqualTo(FLOAT_PERCENTAGE_BETWEEN_ZERO_ONE)
    }

    // negative test
    @Test
    fun `Format float percentage, float input has to be greater than 0f`() {
        val testFloat = -2.4567f
        val e = assertThrows(IllegalArgumentException::class.java) {
            testFloat.format(places = 2, percentage = true)
        }
        assertThat(e).hasMessageThat().isEqualTo(FLOAT_PERCENTAGE_BETWEEN_ZERO_ONE)
    }

    // negative test
    @Test
    fun `Format float, places have to be greater than 0f`() {
        val testFloat = -2.4567f
        val e = assertThrows(IllegalArgumentException::class.java) {
            testFloat.format(places = -1, percentage = false)
        }
        assertThat(e).hasMessageThat().isEqualTo(FAILED_REQUIREMENT)
    }
}
