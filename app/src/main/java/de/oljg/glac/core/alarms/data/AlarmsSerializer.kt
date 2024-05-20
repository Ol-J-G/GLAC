package de.oljg.glac.core.alarms.data

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
class AlarmsSerializer(
    private val serializer: KSerializer<Alarm>,
) : KSerializer<PersistentList<Alarm>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<Alarm>>() {
        @ExperimentalSerializationApi
        override val serialName = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<Alarm>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<Alarm> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }
}
