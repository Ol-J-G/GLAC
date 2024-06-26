package de.oljg.glac.feature_clock.domain.model.serializer

import androidx.datastore.core.Serializer
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object ClockSettingsSerializer : Serializer<ClockSettings> {

    override val defaultValue: ClockSettings
        get() = ClockSettings()

    override suspend fun readFrom(input: InputStream): ClockSettings {
        return try {
            Json.decodeFromString(
                deserializer = ClockSettings.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: ClockSettings, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = ClockSettings.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}