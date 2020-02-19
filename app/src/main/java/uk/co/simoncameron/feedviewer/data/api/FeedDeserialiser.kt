package uk.co.simoncameron.feedviewer.data.api

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import uk.co.simoncameron.feedviewer.data.common.WidgetType
import uk.co.simoncameron.feedviewer.data.dto.FeedDTO
import uk.co.simoncameron.feedviewer.data.dto.ImageWidgetDTO
import uk.co.simoncameron.feedviewer.data.dto.SliiderDTO
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class FeedDeserializer : JsonDeserializer<FeedDTO> {

    // @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, ctx: JsonDeserializationContext): FeedDTO {
        val jsonObject = json.asJsonObject
        val gson = Gson()

        val type = (typeOfT as ParameterizedType).actualTypeArguments[0]


        val typeString = jsonObject.get("type").asString
        val dataType = WidgetType.values().first { it.name == typeString }

        return when(dataType) {
            WidgetType.IMAGE -> ctx.deserialize<ImageWidgetDTO>(jsonObject, type)
            WidgetType.SLIIDER -> ctx.deserialize<SliiderDTO>(jsonObject, type)
        }

//        val list: List<FeedDTO> = jsonObject.asJsonArray.map {
//
//        }
//
//        return list

//        val percent = PercentResponse()
//        percent.orderId = jsonObject.get("orderId").asInt
//        percent.type = jsonObject.get("type").asInt
//        val noteString = jsonObject.get("note")
//        if (!noteString.isJsonNull) {
//            percent.note = noteString.asString
//        }
//
//        val typePercent = TypePercentInspection()
//
//        val type = jsonObject.get("type").asInt
//
//        if (1 == type) {
//            val percentTypes = jsonObject.getAsJsonArray("percent")
//            for (percent in percentTypes) {
//                val percentClass= gson.fromJson(percent, Type1::class.java)
//                typePercent.type1.add(percentClass)
//            }
//        }
//
//        if (2 == type) {
//            val percentTypes = jsonObject.getAsJsonArray("percent")
//            for (percent in percentTypes) {
//                val percentClass= gson.fromJson(percent, ItemType2::class.java)
//                typePercent.type2.add(percentClass)
//            }
//        }
//
//        percent.percent = typePercent
//
//        return percent
    }
}