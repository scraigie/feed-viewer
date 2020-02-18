package uk.co.simoncameron.feedviewer.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

interface MviView<S:State> {
    fun render(state: S)
    fun handleSideEffect(effectData: Any)
}

abstract class MviFragment<VM: MviViewModel<*,S>, S: State> : Fragment(), MviView<S> {

    protected abstract val viewModel: VM

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.apply {
            stateLiveData.observe(viewLifecycleOwner, Observer<S> { render(it) })
            sideEffectLiveData.observe(viewLifecycleOwner, Observer<Any> { handleSideEffect(it) })
        }
    }

    override fun handleSideEffect(effectData: Any) { }
}
