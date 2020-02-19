package uk.co.simoncameron.feedviewer.data.feed

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import uk.co.simoncameron.feedviewer.data.api.ApiService
import uk.co.simoncameron.feedviewer.data.dto.FeedDTO
import uk.co.simoncameron.feedviewer.data.dto.ImageWidgetDTO
import uk.co.simoncameron.feedviewer.data.dto.SliiderWidgetDTO
import uk.co.simoncameron.feedviewer.domain.pojo.FeedItem
import uk.co.simoncameron.feedviewer.domain.pojo.Image
import uk.co.simoncameron.feedviewer.domain.pojo.ImageItem
import uk.co.simoncameron.feedviewer.domain.pojo.SliiderItem

interface ContentRepository {
    fun getFeedContent() : Observable<List<FeedItem>>

    class Impl(private val api: ApiService): ContentRepository {

        override fun getFeedContent(): Observable<List<FeedItem>> {
            return api.getFeed()
                .subscribeOn(Schedulers.io())
                .map { it.items }
                .map { feedList ->
                    feedList.map { feedItem ->
                        when(feedItem) {
                            is ImageWidgetDTO -> feedItem.toImageItem()
                            is SliiderWidgetDTO -> feedItem.toSliiderItem(feedList)
                        }
                    }
                }
                .toObservable()
        }

        private fun ImageWidgetDTO.toImageItem() =
            ImageItem(
                title = title,
                deepLink = deepLink ?: url ?: "",  // Some consistency from the api would be nice! :P
                images = images.map { Image(url = it.imageUrl ) },
                dataType = dataType)

        private fun SliiderWidgetDTO.toSliiderItem(feedList: List<FeedDTO>): SliiderItem {
            val imageIds = sliiderImages.map { it.id }

            return SliiderItem(
                images = feedList
                    .filter { it is ImageWidgetDTO && it.id in imageIds }
                    .map { it as ImageWidgetDTO }
                    .map { it.copy(images = it.images.take(MAX_IMAGES_IN_SLIIDER_ITEM)) }
                    .map { it.toImageItem() })
        }

        companion object {
            private const val MAX_IMAGES_IN_SLIIDER_ITEM = 1
        }
    }
}