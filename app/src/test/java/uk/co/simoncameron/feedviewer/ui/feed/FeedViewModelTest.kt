package uk.co.simoncameron.feedviewer.ui.feed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.co.simoncameron.feedviewer.data.common.ContentDataType
import uk.co.simoncameron.feedviewer.domain.feed.FeedInteractor
import uk.co.simoncameron.feedviewer.domain.pojo.FeedItem
import uk.co.simoncameron.feedviewer.domain.pojo.ImageItem

class FeedViewModelTest {

    lateinit var feedViewModel: FeedViewModel

    private val mockFeedInteractor: FeedInteractor = mock()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val stateObserverSpy = spy(Observer<FeedState> {  })
    private val effectsObserverSpy = spy(Observer<FeedEffects> {  })

    @Before
    fun setUp() {

        feedViewModel = FeedViewModel(mockFeedInteractor)

//        whenever(mockFeedInteractor.loadContent()) doReturn Observable.empty()

        feedViewModel.stateLiveData.observeForever(stateObserverSpy)
        feedViewModel.sideEffectLiveData.observeForever(effectsObserverSpy)

        RxAndroidPlugins.setInitMainThreadSchedulerHandler { h -> Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { h -> Schedulers.trampoline() }

        feedViewModel.init()
    }

    @After
    fun tearDown() {
        reset(mockFeedInteractor)

        feedViewModel.stateLiveData.removeObserver(stateObserverSpy)
        feedViewModel.sideEffectLiveData.removeObserver(effectsObserverSpy)

        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun `on successful load of content results in state event being pushed`() {

        whenever(mockFeedInteractor.loadContent()) doReturn Observable.just(mockedValidContent)

        feedViewModel.loadContent()

        inOrder(stateObserverSpy, effectsObserverSpy) {
            verify(stateObserverSpy).onChanged(feedStateFactory(isLoading = true))
            verify(stateObserverSpy).onChanged(feedStateFactory(isLoading = false, content = mockedValidContent))
        }
    }

    @Test
    fun `on error loading content results in error state being pushed`() {

        whenever(mockFeedInteractor.loadContent()) doReturn Observable.error(Throwable("Unable to fetch content"))

        feedViewModel.loadContent()

        inOrder(stateObserverSpy, effectsObserverSpy) {
            verify(stateObserverSpy).onChanged(feedStateFactory(isLoading = true))
            verify(stateObserverSpy).onChanged(feedStateFactory(isLoading = false, retryVisible = true))
        }
    }

    private val mockedValidContent =
        listOf<FeedItem>( mock(), mock(), mock() )

    private fun feedStateFactory(
        isLoading: Boolean = false,
        content: List<FeedItem> = listOf(),
        retryVisible: Boolean = false) = FeedState(isLoading, content, retryVisible)

    private fun feedItemFactory(dataType: ContentDataType = ContentDataType.NEWS) =
        ImageItem("title", "deepLink", listOf(), dataType)
}