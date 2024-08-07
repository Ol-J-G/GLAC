package de.oljg.glac.feature_help.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.BulletPointRow
import de.oljg.glac.feature_help.ui.utils.HelpScreenDefaults.IMPORTANT_NOTES_PADDING
import de.oljg.glac.feature_help.ui.utils.HelpScreenDefaults.IMPORTANT_NOTES_SURFACE_CORNER_SIZE
import de.oljg.glac.feature_help.ui.utils.HelpScreenDefaults.IMPORTANT_NOTES_SURFACE_ELEVATION
import de.oljg.glac.feature_help.ui.utils.HelpScreenDefaults.OUTER_COLUMN_ELEMENTS_SPACE
import de.oljg.glac.feature_help.ui.utils.HelpScreenDefaults.OUTER_SURFACE_PADDING
import de.oljg.glac.feature_help.ui.utils.HelpScreenDefaults.RECOMMENDED_USE_ELEMENTS_SPACE
import de.oljg.glac.feature_help.ui.utils.HelpScreenDefaults.SPACER_HEIGHT


@Composable
fun HelpScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(OUTER_SURFACE_PADDING),
        color = MaterialTheme.colorScheme.background
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(OUTER_COLUMN_ELEMENTS_SPACE)
        ) {
            Surface(
                shape = RoundedCornerShape(IMPORTANT_NOTES_SURFACE_CORNER_SIZE),
                tonalElevation = IMPORTANT_NOTES_SURFACE_ELEVATION
            ) {
                Column {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = IMPORTANT_NOTES_PADDING),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.important_notes).uppercase(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Column(
                        modifier = Modifier.padding(IMPORTANT_NOTES_PADDING),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(IMPORTANT_NOTES_PADDING)
                    ) {
                        Text(
                            text = stringResource(R.string.important_notes_p1),
                            textAlign = TextAlign.Justify
                        )
                        Text(
                            text = stringResource(R.string.important_notes_p2),
                            textAlign = TextAlign.Justify
                        )
                        Text(
                            text = stringResource(R.string.important_notes_p3),
                            textAlign = TextAlign.Justify
                        )
                        Text(
                            text = stringResource(R.string.important_notes_p4),
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(SPACER_HEIGHT))
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(RECOMMENDED_USE_ELEMENTS_SPACE)
            ) {
                Text(
                    text = stringResource(R.string.recommended_use),
                    style = MaterialTheme.typography.titleLarge
                )
                BulletPointRow(
                    number = stringResource(R.string._1),
                    text = stringResource(R.string.recommended_use_p1)
                )
                BulletPointRow(
                    number = stringResource(R.string._2),
                    text = stringResource(R.string.recommended_use_p2)
                )
                BulletPointRow(
                    number = stringResource(R.string._3),
                    text = stringResource(R.string.recommended_use_p3)
                )
                BulletPointRow(
                    number = stringResource(R.string._4),
                    text = stringResource(R.string.recommended_use_p4)
                )
                BulletPointRow(
                    number = stringResource(R.string._5),
                    text = stringResource(R.string.recommended_use_p5)
                )
            }
        }
    }
}
