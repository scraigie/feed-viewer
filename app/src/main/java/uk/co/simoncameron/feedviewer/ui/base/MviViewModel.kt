package uk.co.simoncameron.feedviewer.ui.base


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import uk.co.simoncameron.feedviewer.utils.livedata.SingleLiveEvent

abstract class MviViewModel<A: Action, S: State>(initialState: S) : ViewModel() {

    protected val actionsSubject = PublishSubject.create<A>()
    protected val sideEffectEvent = SingleLiveEvent<Any>()

    abstract val actionProcessor: (A) -> Observable<Reducer<S>>

    private val _stateLiveData =
        actionsSubject
            .flatMap { actionProcessor(it) }
            .scan<S>(initialState) { previous, reducer -> reducer(previous) }
            .distinctUntilChanged()
            .toFlowable(BackpressureStrategy.BUFFER)
            .toLiveData()


    val sideEffectLiveData
        get(): LiveData<Any> = sideEffectEvent

    val stateLiveData
        get():LiveData<S> = _stateLiveData
}
