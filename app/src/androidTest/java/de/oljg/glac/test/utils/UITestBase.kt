package de.oljg.glac.test.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry

open class UITestBase {
    protected fun stringRes(@StringRes resId: Int): String {
        return UITestUtil.context.getString(resId)
    }

    internal object UITestUtil {
        val context: Context = InstrumentationRegistry.getInstrumentation().context
    }



}