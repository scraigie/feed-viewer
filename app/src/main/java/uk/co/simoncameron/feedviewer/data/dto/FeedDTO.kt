package uk.co.simoncameron.feedviewer.data.dto

import com.google.gson.annotations.SerializedName
import uk.co.simoncameron.feedviewer.data.common.ContentDataType
import uk.co.simoncameron.feedviewer.data.common.WidgetType

sealed class FeedDTO

data class SliiderDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val widgetType: WidgetType,
    @SerializedName("images") val sliiderImages: List<SliiderImageDTO>
) : FeedDTO()

data class SliiderImageDTO(
    @SerializedName("id") val id: Int
)

data class ImageWidgetDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val widgetType: WidgetType,
    @SerializedName("datatype") val dataType: ContentDataType,
    @SerializedName("title") val title: String,
    @SerializedName("deep_link") val deepLink: String,
    @SerializedName("images") val images: List<ImageDTO>
) : FeedDTO()

data class ImageDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("url") val imageUrl: String
)