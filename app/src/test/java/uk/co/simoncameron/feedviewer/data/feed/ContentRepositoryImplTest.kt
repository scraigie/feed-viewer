package uk.co.simoncameron.feedviewer.data.feed

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
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
import uk.co.simoncameron.feedviewer.data.dto.ImageWidgetDTO
import uk.co.simoncameron.feedviewer.data.dto.SliiderDTO
import uk.co.simoncameron.feedviewer.data.dto.SliiderImageDTO
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
        reset(apiService)
        RxJavaPlugins.reset()
    }

    @Test
    fun `should fetch response from api and map DTO object to domain`() {

        whenever(apiService.getFeed()) doReturn Single.just(mockApiFeed)

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
        ImageWidgetDTO(id = 1, widgetType = WidgetType.IMAGE, dataType = ContentDataType.NEWS, title = "item1 title", deepLink = "deeplink1",images = listOf()),
        ImageWidgetDTO(id = 2, widgetType = WidgetType.IMAGE, dataType = ContentDataType.PREMIUM_NEWS, title = "item2 title", deepLink = "deeplink2",images = listOf()),
        ImageWidgetDTO(id = 3, widgetType = WidgetType.IMAGE, dataType = ContentDataType.ADS, title = "item3 title", deepLink = "deeplink3",images = listOf()),
        SliiderDTO(id = 4, widgetType = WidgetType.SLIIDER, sliiderImages = listOf(
            SliiderImageDTO(id = 1),
            SliiderImageDTO(id = 2),
            SliiderImageDTO(id = 3)
            )
        )
    )

    private val feedItemFactory = listOf(
        ImageItem(dataType = ContentDataType.NEWS),
        ImageItem(dataType = ContentDataType.PREMIUM_NEWS),
        ImageItem(dataType = ContentDataType.ADS),
        SliiderItem(images = listOf(
            ImageItem(dataType = ContentDataType.NEWS),
            ImageItem(dataType = ContentDataType.PREMIUM_NEWS),
            ImageItem(dataType = ContentDataType.ADS)
        ))
    )
}