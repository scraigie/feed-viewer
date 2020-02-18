package uk.co.simoncameron.feedviewer.ui.base


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import io.reactivex.BackpressureStrategy
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.PublishSubject
import uk.co.simoncameron.feedviewer.utils.livedata.SingleLiveEvent

abstract class MviViewModel<A: Action, E: Effect, S: State>(initialState: S) : ViewModel() {

    protected val actionsSubject = PublishSubject.create<A>()
    protected val sideEffectEvent = SingleLiveEvent<E>()

    abstract val actionProcessor: ObservableTransformer<A,Reducer<S>>

    private val _stateLiveData by lazy {
        actionsSubject
            .compose(actionProcessor)
            .scan<S>(initialState) { previous, reducer -> reducer(previous) }
            .doOnNext{ println(it) }
            .distinctUntilChanged()
            .toFlowable(BackpressureStrategy.BUFFER)
            .toLiveData()
    }

    val sideEffectLiveData
        get(): LiveData<E> = sideEffectEvent

    val stateLiveData
        get():LiveData<S> = _stateLiveData
}
