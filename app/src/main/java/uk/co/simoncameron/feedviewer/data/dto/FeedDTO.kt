package uk.co.simoncameron.feedviewer.data.dto

import com.google.gson.annotations.SerializedName
import uk.co.simoncameron.feedviewer.data.common.ContentDataType
import uk.co.simoncameron.feedviewer.data.common.WidgetType

data class FeedResponse(
    val items: List<FeedDTO>
)

sealed class FeedDTO

data class SliiderWidgetDTO(
    @SerializedName("type") val widgetType: WidgetType,
    @SerializedName("images") val sliiderImages: List<SliiderImageDTO>
) : FeedDTO()

data class ImageWidgetDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val widgetType: WidgetType,
    @SerializedName("datatype") val dataType: ContentDataType,
    @SerializedName("title") val title: String,
    @SerializedName("deep_link") val deepLink: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("images") val images: List<ImageDTO>
) : FeedDTO()

data class ImageDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("url") val imageUrl: String
)

data class SliiderImageDTO(
    @SerializedName("id") val id: Int
)