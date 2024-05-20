package de.oljg.glac.core.alarms.data

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AlarmSettingsSerializer : Serializer<AlarmSettings> {

    override val defaultValue: AlarmSettings
        get() = AlarmSettings()

    override suspend fun readFrom(input: InputStream): AlarmSettings {
        return try {
            Json.decodeFromString(
                deserializer = AlarmSettings.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AlarmSettings, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = AlarmSettings.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}
