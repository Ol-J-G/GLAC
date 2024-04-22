package de.oljg.glac.core.settings.data

import android.content.Context
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.memberProperties


val Context.dataStore by dataStore("clock-settings.json", ClockSettingsSerializer)
class ClockSettingsRepository(
    private val context: Context
) {
    fun clockSettingsFlow(): Flow<ClockSettings> {
        return context.dataStore.data
    }

    suspend fun setShowSeconds(value: Boolean) {
        context.dataStore.updateData {
            it.copy(showSeconds = value)
        }
    }

    //TODO: introduce generic fun to set data class fields
//    suspend fun <T> set(field: String, newValue: T) {
//        val clockSettingsFields = ClockSettings::class.java.declaredFields.toList()
//        clockSettingsFields.find { f -> f.name == field }
//
//
//        context.dataStore.updateData {
//            it
//        }
//    }


    /**
     * https://stackoverflow.com/questions/49042656/simple-code-to-copy-same-name-properties
     */
//    fun <T : Any, R : Any> T.copyPropsFrom(fromObject: R, vararg props: KProperty<*>) {
//        // only consider mutable properties
//        val mutableProps = this::class.memberProperties.filterIsInstance<KMutableProperty<*>>()
//        // if source list is provided use that otherwise use all available properties
//        val sourceProps = if (props.isEmpty()) fromObject::class.memberProperties else props.toList()
//        // copy all matching
//        mutableProps.forEach { targetProp ->
//            sourceProps.find {
//                // make sure properties have same name and compatible types
//                it.name == targetProp.name && targetProp.returnType.isSupertypeOf(it.returnType)
//            }?.let { matchingProp ->
//                targetProp.setter.call(this, matchingProp.getter.call(fromObject))
//            }
//        }
//    }

}