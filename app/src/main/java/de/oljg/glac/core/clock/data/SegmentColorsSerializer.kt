package de.oljg.glac.core.clock.data

import de.oljg.glac.clock.digital.ui.utils.Segment
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Suppress("EXTERNAL_SERIALIZER_USELESS")
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentMap::class)
class SegmentColorsSerializer(
    private val keySerializer: KSerializer<Segment>,
    private val valueSerializer: KSerializer<Int>
) : KSerializer<PersistentMap<Segment, Int>> {

    private class PersistentMapDescriptor :
        SerialDescriptor by serialDescriptor<Map<Segment, Int>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentMap"
    }
    override val descriptor: SerialDescriptor = PersistentMapDescriptor()

    override fun deserialize(decoder: Decoder): PersistentMap<Segment, Int> {
        return MapSerializer(keySerializer, valueSerializer).deserialize(decoder).toPersistentMap()
    }

    override fun serialize(encoder: Encoder, value: PersistentMap<Segment, Int>) {
        return MapSerializer(keySerializer, valueSerializer).serialize(encoder, value)
    }
}
