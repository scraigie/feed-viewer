package uk.co.simoncameron.feedviewer.data.feed

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.co.simoncameron.feedviewer.data.api.ApiService
import uk.co.simoncameron.feedviewer.data.common.ContentDataType
import uk.co.simoncameron.feedviewer.data.common.WidgetType
import uk.co.simoncameron.feedviewer.data.dto.FeedResponse
import uk.co.simoncameron.feedviewer.data.dto.ImageWidgetDTO
import uk.co.simoncameron.feedviewer.data.dto.SliiderImageDTO
import uk.co.simoncameron.feedviewer.data.dto.SliiderWidgetDTO
import uk.co.simoncameron.feedviewer.domain.pojo.ImageItem
import uk.co.simoncameron.feedviewer.domain.pojo.SliiderItem

class ContentRepositoryImplTest {

    private lateinit var contentRepository: ContentRepository.Impl

    private val apiService: ApiService = mock()

    @Before
    fun setUp() {
        contentRepository = ContentRepository.Impl(apiService)

        RxJavaPlugins.setIoSchedulerHandler { h -> Schedulers.trampoline() }
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    @Test
    fun `should fetch response from api and map DTO object to domain`() {

        whenever(apiService.getFeed()) doReturn Single.just(FeedResponse(mockApiFeed))

        val feedItemList = feedItemFactory

        contentRepository.getFeedContent().test()
            .assertValueCount(1)
            .assertNoErrors()
            .assertValue(feedItemList)
            .dispose()
    }

    @Test
    fun `should return error if api throws error`() {

        val error = Throwable("an error occurred")
        whenever(apiService.getFeed()) doReturn Single.error(error)

        contentRepository.getFeedContent().test()
            .assertValueCount(0)
            .assertError(error)
            .dispose()
    }

    private val mockApiFeed = listOf(
        ImageWidgetDTO(id = 1, widgetType = WidgetType.IMAGE, dataType = ContentDataType.NEWS, title = "item1 title", deepLink = "deeplink1",images = listOf(), url = null),
        ImageWidgetDTO(id = 2, widgetType = WidgetType.IMAGE, dataType = ContentDataType.PREMIUM_NEWS, title = "item2 title", deepLink = null ,images = listOf(), url = "deeplink2"),
        ImageWidgetDTO(id = 3, widgetType = WidgetType.IMAGE, dataType = ContentDataType.ADS, title = "item3 title", deepLink = "deeplink3",images = listOf(), url = ""),
        SliiderWidgetDTO(widgetType = WidgetType.SLIIDER, sliiderImages = listOf(
            SliiderImageDTO(id = 1),
            SliiderImageDTO(id = 2),
            SliiderImageDTO(id = 3)
            )
        )
    )

    private val feedItemFactory = listOf(
        ImageItem(title = "item1 title", deepLink = "deeplink1", images = listOf(), dataType = ContentDataType.NEWS),
        ImageItem(title = "item2 title", deepLink = "deeplink2", images = listOf(), dataType = ContentDataType.PREMIUM_NEWS),
        ImageItem(title = "item3 title", deepLink = "deeplink3", images = listOf(), dataType = ContentDataType.ADS),
        SliiderItem(images = listOf(
            ImageItem(title = "item1 title", deepLink = "deeplink1", images = listOf(), dataType = ContentDataType.NEWS),
            ImageItem(title = "item2 title", deepLink = "deeplink2", images = listOf(), dataType = ContentDataType.PREMIUM_NEWS),
            ImageItem(title = "item3 title", deepLink = "deeplink3", images = listOf(), dataType = ContentDataType.ADS)
        ))
    )
}