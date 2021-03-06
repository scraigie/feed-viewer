package uk.co.simoncameron.feedviewer.ui.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

interface MviView<E:Effect,S:State> {
    fun render(state: S)
    fun handleSideEffect(effect: E)
}

abstract class MviFragment<VM: MviViewModel<*,E, S>, E: Effect, S: State> : Fragment(), MviView<E,S> {

    protected abstract val viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            init()
            stateLiveData.observe(viewLifecycleOwner, Observer<S> { render(it) })
            sideEffectLiveData.observe(viewLifecycleOwner, Observer<E> { handleSideEffect(it) })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun handleSideEffect(effect: E) { }

    override fun render(state: S) { }
}
