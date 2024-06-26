package de.oljg.glac

import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Workaround to be able to run isolated UI tests
 * with createAndroidComposeRule<EmptyTestActivity>() where
 * in a "Hilt" project it seems not to be possible to use
 * createComposeRule() with androidx.test.runner.AndroidJUnitRunner together
 * with a HiltTestRunner and @HiltAndroidTest's ...
 *
 * Isolated tests are going to run in this empty activity then ...
 */
@AndroidEntryPoint
class EmptyTestActivity : ComponentActivity()
