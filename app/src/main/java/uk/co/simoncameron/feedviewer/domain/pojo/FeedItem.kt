package uk.co.simoncameron.feedviewer.domain.pojo

import uk.co.simoncameron.feedviewer.data.common.ContentDataType

sealed class FeedItem

data class ImageItem(val dataType: ContentDataType) : FeedItem()

data class SliiderItem(val images: List<ImageItem>) : FeedItem()

