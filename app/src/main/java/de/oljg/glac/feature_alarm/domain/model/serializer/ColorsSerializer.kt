package de.oljg.glac.feature_alarm.domain.model.serializer

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Suppress("EXTERNAL_SERIALIZER_USELESS")
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class ColorsSerializer(
    private val serializer: KSerializer<Int>,
) : KSerializer<PersistentList<Int>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<Int>>() {
        @ExperimentalSerializationApi
        override val serialName = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<Int>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<Int> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }
}
