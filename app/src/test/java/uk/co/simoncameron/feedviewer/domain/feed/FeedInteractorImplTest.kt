package uk.co.simoncameron.feedviewer.domain.feed

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.co.simoncameron.feedviewer.data.common.ContentDataType
import uk.co.simoncameron.feedviewer.data.db.UserRole
import uk.co.simoncameron.feedviewer.data.feed.ContentRepository
import uk.co.simoncameron.feedviewer.data.preferences.AppPreferences
import uk.co.simoncameron.feedviewer.data.user.UserRepository
import uk.co.simoncameron.feedviewer.domain.pojo.FeedItem
import uk.co.simoncameron.feedviewer.domain.pojo.ImageItem
import uk.co.simoncameron.feedviewer.domain.pojo.SliiderItem
import uk.co.simoncameron.feedviewer.domain.pojo.User

class FeedInteractorImplTest {

    private lateinit var feedInteractor: FeedInteractor.Impl

    private val mockUserRepository: UserRepository = mock()

    private val mockAppPreferences: AppPreferences = mock()

    private val mockContentRepository: ContentRepository = mock()

    @Before
    fun setUp() {

        feedInteractor = FeedInteractor.Impl(mockAppPreferences, mockUserRepository, mockContentRepository)

//        RxJavaPlugins.setComputationSchedulerHandler { h -> Schedulers.trampoline() }
    }

    @After
    fun tearDown() {

        reset(mockUserRepository, mockContentRepository)
//        RxJavaPlugins.reset()
    }

    @Test
    fun `if user has role ROLE_NORMAL then content will return items of type ADS and NEWS`() {

        val username = "username"

        whenever(mockUserRepository.getUser(username)) doReturn
                Observable.just(User(
                    username = "username",
                    password = "password",
                    role = UserRole.ROLE_NORMAL))

        whenever(mockAppPreferences.user) doReturn username

        whenever(mockContentRepository.getFeedContent()) doReturn
                Observable.just(feedItems)


        feedInteractor.loadContent().test()
            .assertValueCount(1)
            .assertNoErrors()
            .assertValue(feedItems.filterForDataTypes(ContentDataType.ADS, ContentDataType.NEWS))
            .dispose()
    }

    @Test
    fun `if user has role ROLE_PREMIUM then content will return items of type ADS and NEWS`() {

        val username = "username"

        whenever(mockUserRepository.getUser(username)) doReturn
                Observable.just(User(
                    username = "username",
                    password = "password",
                    role = UserRole.ROLE_PREMIUM))

        whenever(mockAppPreferences.user) doReturn username

        whenever(mockContentRepository.getFeedContent()) doReturn
                Observable.just(feedItems)


        feedInteractor.loadContent().test()
            .assertValueCount(1)
            .assertNoErrors()
            .assertValue(feedItems.filterForDataTypes(ContentDataType.NEWS, ContentDataType.PREMIUM_NEWS))
            .dispose()
    }

    private val feedItems = listOf(
        imageItemFactory(ContentDataType.ADS),
        imageItemFactory(ContentDataType.NEWS),
        imageItemFactory(ContentDataType.PREMIUM_NEWS),
        sliiderItemFactory(
            imageItemFactory(ContentDataType.ADS),
            imageItemFactory(ContentDataType.NEWS),
            imageItemFactory(ContentDataType.PREMIUM_NEWS)
        )
    )

    private fun imageItemFactory(dataType: ContentDataType) =
        ImageItem(dataType = dataType)

    private fun sliiderItemFactory(vararg imageItems: ImageItem) =
        SliiderItem(images = imageItems.asList())

    //TODO replace with concrete types?
    private fun List<FeedItem>.filterForDataTypes(vararg dataTypes: ContentDataType) =
        filter {
            when (it) {
                is ImageItem -> it.dataType in dataTypes
                else -> true
            }
        }
        .map { feedItem ->
            when (feedItem) {
                is SliiderItem -> feedItem.copy(images = feedItem.images
                    .filter { it.dataType in dataTypes })
                else -> feedItem
            }
        }
}