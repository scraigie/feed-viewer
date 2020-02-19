package uk.co.simoncameron.feedviewer.data.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import uk.co.simoncameron.feedviewer.data.common.WidgetType
import uk.co.simoncameron.feedviewer.data.dto.FeedResponse
import uk.co.simoncameron.feedviewer.data.dto.ImageWidgetDTO
import uk.co.simoncameron.feedviewer.data.dto.SliiderWidgetDTO
import java.lang.reflect.Type
import java.util.*

class FeedDeserializer : JsonDeserializer<FeedResponse> {

    //TODO untested
     @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, ctx: JsonDeserializationContext): FeedResponse {

        val items = json.asJsonArray
            .filter { jsonElement ->
                val typeString = jsonElement.asJsonObject.get("type").asString

                typeString in WidgetType.values().map { it.name.toLowerCase(Locale.ROOT) }
            }
            .map { jsonElement ->
                val typeString = jsonElement.asJsonObject.get("type").asString
                val dataType = WidgetType.values().first { it.name.toLowerCase(Locale.ROOT) == typeString }

                when(dataType) {
                    WidgetType.IMAGE -> ctx.deserialize<ImageWidgetDTO>(jsonElement.asJsonObject, ImageWidgetDTO::class.java)
                    WidgetType.SLIIDER -> ctx.deserialize<SliiderWidgetDTO>(jsonElement.asJsonObject, SliiderWidgetDTO::class.java)
                }
        }

        return FeedResponse(items = items)
    }
}