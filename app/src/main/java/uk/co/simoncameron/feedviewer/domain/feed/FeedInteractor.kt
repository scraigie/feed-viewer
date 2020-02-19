package uk.co.simoncameron.feedviewer.domain.feed

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import uk.co.simoncameron.feedviewer.data.common.ContentDataType
import uk.co.simoncameron.feedviewer.data.db.UserRole
import uk.co.simoncameron.feedviewer.data.feed.ContentRepository
import uk.co.simoncameron.feedviewer.data.preferences.AppPreferences
import uk.co.simoncameron.feedviewer.data.user.UserRepository
import uk.co.simoncameron.feedviewer.domain.pojo.FeedItem
import uk.co.simoncameron.feedviewer.domain.pojo.ImageItem
import uk.co.simoncameron.feedviewer.domain.pojo.SliiderItem

interface FeedInteractor {
    fun loadContent() : Observable<List<FeedItem>>

    class Impl(
        private val appPreferences: AppPreferences,
        private val userRepository: UserRepository,
        private val contentRepository: ContentRepository): FeedInteractor {

        override fun loadContent(): Observable<List<FeedItem>> {

            return Observable.combineLatest(
                Observable.just(appPreferences.user)
                    .switchMap { userRepository.getUser(it) }
                    .map { it.role },
                contentRepository.getFeedContent(),
                BiFunction { role, feed -> feed.filterForDataTypes(role.allowableDataTypes()) }
            )
        }

        private fun UserRole.allowableDataTypes(): List<ContentDataType> =
            when(this) {
                UserRole.ROLE_NORMAL -> listOf(ContentDataType.ADS, ContentDataType.NEWS)
                UserRole.ROLE_PREMIUM -> listOf(ContentDataType.NEWS, ContentDataType.PREMIUM_NEWS)
            }

        private fun List<FeedItem>.filterForDataTypes(dataTypes: List<ContentDataType>) =
            filter {
                when (it) {
                    is ImageItem -> it.dataType in dataTypes
                    else -> true
                }
            }
                .map {
                    when (it) {
                        is SliiderItem -> it.copy(images = it.images
                            .filter { it.dataType in dataTypes })
                        else -> it
                    }
                }
    }
}