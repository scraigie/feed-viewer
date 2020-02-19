package uk.co.simoncameron.feedviewer.ui.feed

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import uk.co.simoncameron.feedviewer.domain.feed.FeedInteractor
import uk.co.simoncameron.feedviewer.domain.pojo.FeedItem
import uk.co.simoncameron.feedviewer.ui.base.*
import uk.co.simoncameron.feedviewer.ui.feed.FeedState.Companion.INITIAL_STATE

data class FeedState(
    val isLoading: Boolean,
    val content: List<FeedItem>,
    val retryVisible: Boolean
): State {
    companion object {
        val INITIAL_STATE = FeedState(false, listOf(), false)
    }
}

sealed class FeedActions: Action {
    object LoadContent: FeedActions()
    data class FeedClicked(
        val url: String
    ): FeedActions()
}

sealed class FeedEffects: Effect {
    data class ViewContent(
        val url: String
    ): FeedEffects()
    object ContentError: FeedEffects()
}

object FeedReducers {
    fun loadingReducer(): Reducer<FeedState> = { it.copy(isLoading = true) }
    fun successReducer(data: List<FeedItem>): Reducer<FeedState> = { it.copy(content = data, isLoading = false) }
    fun failureReducer(): Reducer<FeedState> = { it.copy(isLoading = false, retryVisible = true) }
}

class FeedViewModel(private val feedInteractor: FeedInteractor): MviViewModel<FeedActions, FeedEffects, FeedState>(INITIAL_STATE) {

    fun loadContent() {
        actionsSubject.onNext(FeedActions.LoadContent)
    }

    fun itemClicked(url: String) {
        actionsSubject.onNext(FeedActions.FeedClicked(url))
    }

    override val actionProcessor = ObservableTransformer<FeedActions, Reducer<FeedState>>{
        it.publish { connectibleObs -> Observable.merge(
            connectibleObs.ofType(FeedActions.LoadContent::class.java)
                .switchMap {
                    feedInteractor.loadContent()
                        .map(FeedReducers::successReducer)
                        .startWith(FeedReducers.loadingReducer())
                        .onErrorReturn { FeedReducers.failureReducer() }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                },
            connectibleObs.ofType(FeedActions.FeedClicked::class.java)
                .map { { _: FeedState -> INITIAL_STATE } }
        ) }
    }
}