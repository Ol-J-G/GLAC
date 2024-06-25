package de.oljg.glac.feature_alarm.ui.utils

import com.google.common.truth.Truth.assertThat
import de.oljg.glac.util.january_2024_of
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * Note: Sketches in comments are not to scale!
 */
class OverlappingRangesTest {
    private lateinit var range: OpenEndRange<LocalDateTime>

    @Before
    fun init() {
        val rangeStart = january_2024_of(dayOfMonth = 10, hour = 5, minute = 0)
        val rangeEnd = january_2024_of(dayOfMonth = 11, hour = 5, minute = 0)
        range = rangeStart.rangeUntil(rangeEnd)
    }

    /**
     * |-----------range-----------|
     * |-----------range-----------|
     */
    @Test
    fun `range does overlap range`() {
        assertThat(range.overlapsOrContainsCompletely(range)).isTrue()
    }

    /**
     * 10th                        11th
     * 5:00                        5:00
     *  |-----------range-----------|
     *    |-------otherRange------|
     *   6:00                    4:00
     *   10th                    11th
     */
    @Test
    fun `range contains otherRange completely`() {
        val otherRangeStart = january_2024_of(dayOfMonth = 10, hour = 6, minute = 0)
        val otherRangeEnd = january_2024_of(dayOfMonth = 11, hour = 4, minute = 0)
        val otherRange = otherRangeStart.rangeUntil(otherRangeEnd)

        assertThat(range.overlapsOrContainsCompletely(otherRange)).isTrue()
    }

    /**
     *   10th                    11th
     *   5:00                    5:00
     *    |---------range---------|
     *  |---------otherRange--------|
     * 4:00                        6:00
     * 10th                        11th
     */
    @Test
    fun `otherRange contains range completely`() {
        val otherRangeStart = january_2024_of(dayOfMonth = 10, hour = 4, minute = 0)
        val otherRangeEnd = january_2024_of(dayOfMonth = 11, hour = 6, minute = 0)
        val otherRange = otherRangeStart.rangeUntil(otherRangeEnd)

        assertThat(range.overlapsOrContainsCompletely(otherRange)).isTrue()
    }

    /**
     * 10th                        11th
     * 5:00                        5:00
     *  |-----------range-----------|
     *                                   |---------otherRange--------|
     *                                  5:00                        5:00
     *                                  12th                        13th
     */
    @Test
    fun `Ranges don't overlap`() {
        val otherRangeStart = january_2024_of(dayOfMonth = 12, hour = 5, minute = 0)
        val otherRangeEnd = january_2024_of(dayOfMonth = 13, hour = 5, minute = 0)
        val otherRange = otherRangeStart.rangeUntil(otherRangeEnd)

        assertThat(range.overlapsOrContainsCompletely(otherRange)).isFalse()
    }

    /**
     * 10th                        11th
     * 5:00                        5:00
     *  |-----------range-----------|
     *                      |---------otherRange--------|
     *                     0:00                        0:00
     *                     11th                        12th
     */
    @Test
    fun `Ranges do overlap`() {
        val otherRangeStart = january_2024_of(dayOfMonth = 11, hour = 0, minute = 0)
        val otherRangeEnd = january_2024_of(dayOfMonth = 12, hour = 0, minute = 0)
        val otherRange = otherRangeStart.rangeUntil(otherRangeEnd)

        assertThat(range.overlapsOrContainsCompletely(otherRange)).isTrue()
    }

    /**
     * 10th                        11th
     * 5:00                        5:00
     *  |-----------range-----------|
     *                              |---------otherRange--------|
     *                             5:00                        0:00
     *                             11th                        12th
     */
    @Test
    fun `Ranges do overlap, edge case right border`() {
        val otherRangeStart = january_2024_of(dayOfMonth = 11, hour = 5, minute = 0)
        val otherRangeEnd = january_2024_of(dayOfMonth = 12, hour = 0, minute = 0)
        val otherRange = otherRangeStart.rangeUntil(otherRangeEnd)

        assertThat(range.overlapsOrContainsCompletely(otherRange)).isTrue()
    }

    /**
     *                             10th                        11th
     *                             5:00                        5:00
     *                              |-----------range-----------|
     *  |---------otherRange--------|
     * 0:00                        5:00
     * 10th                        10th
     */
    @Test
    fun `Ranges do overlap, edge case left border`() {
        val otherRangeStart = january_2024_of(dayOfMonth = 10, hour = 0, minute = 0)
        val otherRangeEnd = january_2024_of(dayOfMonth = 10, hour = 5, minute = 0)
        val otherRange = otherRangeStart.rangeUntil(otherRangeEnd)

        assertThat(range.overlapsOrContainsCompletely(otherRange)).isTrue()
    }

    /**
     * 10th                        11th
     * 5:00                        5:00
     *  |-----------range-----------|
     *                               |---------otherRange--------|
     *                              5:01                        0:00
     *                              11th                        12th
     */
    @Test
    fun `Ranges don't overlap, edge case right border`() {
        val otherRangeStart = january_2024_of(dayOfMonth = 11, hour = 5, minute = 1)
        val otherRangeEnd = january_2024_of(dayOfMonth = 12, hour = 0, minute = 0)
        val otherRange = otherRangeStart.rangeUntil(otherRangeEnd)

        assertThat(range.overlapsOrContainsCompletely(otherRange)).isFalse()
    }

    /**
     *                              10th                        11th
     *                              5:00                        5:00
     *                               |-----------range-----------|
     *  |---------otherRange--------|
     * 0:00                        4:59
     * 10th                        10th
     */
    @Test
    fun `Ranges don't overlap, edge case left border`() {
        val otherRangeStart = january_2024_of(dayOfMonth = 10, hour = 0, minute = 0)
        val otherRangeEnd = january_2024_of(dayOfMonth = 10, hour = 4, minute = 59)
        val otherRange = otherRangeStart.rangeUntil(otherRangeEnd)

        assertThat(range.overlapsOrContainsCompletely(otherRange)).isFalse()
    }
}
