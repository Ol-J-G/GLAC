package de.oljg.glac.core.alarms.data

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration

class DurationSerializer: KSerializer<Duration> {
    override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Duration", PrimitiveKind.STRING)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(decoder: Decoder): Duration {
        return Duration.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeString(value.toIsoString())
    }
}
