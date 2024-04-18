package de.oljg.glac.clock.digital.ui.utils

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ColorUtilsTest {

    @Test
    fun setSpecifiedColors_Char() {

        // Given, we have some char colors we want to set
        val charColors = mapOf(
            Pair('A', Color.Black),
            Pair('C', Color.Green),
            Pair('E', Color.Yellow)
        )

        // When we try to set/overwrite default colors with the ones in charColor map
        val result = setSpecifiedColors(
            colors = charColors,
            defaultColors = mapOf(
                Pair('A', Color.DarkGray),
                Pair('B', Color.DarkGray),
                Pair('C', Color.DarkGray),
                Pair('D', Color.DarkGray),
                Pair('E', Color.DarkGray),
            )
        )

        /**
         * Then we expect that only the colors specified in charColors have been replaced,
         * and the remaining chars have the same default color as before.
         */
        assertThat(result).containsExactlyEntriesIn(mapOf(
            Pair('A', Color.Black),
            Pair('B', Color.DarkGray),
            Pair('C', Color.Green),
            Pair('D', Color.DarkGray),
            Pair('E', Color.Yellow),
        ))
    }


    @Test
    fun setSpecifiedColors_Segment() {

        // Given, we have defined all segment colors we want to set
        val segmentColors = mapOf(
            Pair(Segment.TOP, Color.Black),
            Pair(Segment.CENTER, Color.Green),
            Pair(Segment.BOTTOM, Color.Yellow),
            Pair(Segment.TOP_LEFT, Color.Red),
            Pair(Segment.TOP_RIGHT, Color.Blue),
            Pair(Segment.BOTTOM_LEFT, Color.Magenta),
            Pair(Segment.BOTTOM_RIGHT, Color.White),
        )

        // When we try to set/overwrite all default colors with the ones in segmentColors map
        val result = setSpecifiedColors(
            colors = segmentColors,
            defaultColors = mapOf(
                Pair(Segment.TOP, Color.DarkGray),
                Pair(Segment.CENTER, Color.DarkGray),
                Pair(Segment.BOTTOM, Color.DarkGray),
                Pair(Segment.TOP_LEFT, Color.DarkGray),
                Pair(Segment.TOP_RIGHT, Color.DarkGray),
                Pair(Segment.BOTTOM_LEFT, Color.DarkGray),
                Pair(Segment.BOTTOM_RIGHT, Color.DarkGray),
            )
        )

        /**
         * Then we expect that all the colors specified in segmentColors have been set
         * (all default colors have been replaced).
         */
        assertThat(result).containsExactlyEntriesIn(segmentColors)
    }
}