package de.oljg.glac.feature_clock.domain.model.serializer

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

/**
 * Needed to serialize charColors persistent map as part of ClockSettings data class.
 * Unless the suppress message below suggests, exception while trying to use persistent map
 * without this serializer:
 * "kotlinx.serialization.SerializationException: Class 'PersistentOrderedMap' is not registered
 * for polymorphic serialization in the scope of 'PersistentMap'.
 * Mark the base class as 'sealed' or register the serializer explicitly."
 *
 * Nice explanation:
 * https://programmerofpersia.medium.com/how-to-serialize-deserialize-a-persistentlist-persistentmap-with-kotlinx-serialization-72a11a226e56
 */
@Suppress("EXTERNAL_SERIALIZER_USELESS")
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentMap::class)
class CharColorsSerializer(
    private val keySerializer: KSerializer<Char>,
    private val valueSerializer: KSerializer<Int>
) : KSerializer<PersistentMap<Char, Int>> {

    private class PersistentMapDescriptor :
        SerialDescriptor by serialDescriptor<Map<Char, Int>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentMap"
    }
    override val descriptor: SerialDescriptor = PersistentMapDescriptor()

    override fun deserialize(decoder: Decoder): PersistentMap<Char, Int> {
        return MapSerializer(keySerializer, valueSerializer).deserialize(decoder).toPersistentMap()
    }

    override fun serialize(encoder: Encoder, value: PersistentMap<Char, Int>) {
        return MapSerializer(keySerializer, valueSerializer).serialize(encoder, value)
    }
}
