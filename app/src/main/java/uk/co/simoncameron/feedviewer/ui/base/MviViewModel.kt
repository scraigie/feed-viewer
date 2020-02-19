package uk.co.simoncameron.feedviewer.ui.base


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import uk.co.simoncameron.feedviewer.utils.livedata.SingleLiveEvent

abstract class MviViewModel<A: Action, E: Effect, S: State>(private val initialState: S) : ViewModel() {

    protected val actionsSubject = PublishRelay.create<A>()

    private val stateSubject = BehaviorRelay.create<S>()

    protected val sideEffectEvent = SingleLiveEvent<E>()

    abstract val actionProcessor: ObservableTransformer<A,Reducer<S>>

    private var flowDisposable: Disposable? = null

    fun init() {
        flowDisposable?.dispose()

        flowDisposable = actionsSubject
            .compose(actionProcessor)
            .scan<S>(initialState) { previous, reducer -> reducer(previous) }
            .distinctUntilChanged()
            .subscribe(stateSubject)
    }

    val sideEffectLiveData
        get(): LiveData<E> = sideEffectEvent

    val stateLiveData
        get():LiveData<S> = stateSubject
                .toFlowable(BackpressureStrategy.BUFFER)
                .toLiveData()

    override fun onCleared() {
        flowDisposable?.dispose()
    }
}
