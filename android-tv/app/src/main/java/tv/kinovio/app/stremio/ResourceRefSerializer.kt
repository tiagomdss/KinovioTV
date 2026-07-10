package tv.kinovio.app.stremio

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

object ResourceRefSerializer : KSerializer<ResourceRef> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ResourceRef", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ResourceRef {
        val input = decoder as? JsonDecoder ?: throw SerializationException("JSON expected")
        return when (val element = input.decodeJsonElement()) {
            is JsonPrimitive -> ResourceRef(element.content)
            is JsonObject -> ResourceRef(element["name"]?.jsonPrimitive?.contentOrNull.orEmpty())
            else -> ResourceRef("")
        }
    }

    override fun serialize(encoder: Encoder, value: ResourceRef) {
        encoder.encodeString(value.name)
    }
}
