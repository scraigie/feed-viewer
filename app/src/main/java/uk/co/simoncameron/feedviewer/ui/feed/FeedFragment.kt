package uk.co.simoncameron.feedviewer.ui.feed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.feed_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import uk.co.simoncameron.feedviewer.R
import uk.co.simoncameron.feedviewer.ui.base.MviFragment


class FeedFragment : MviFragment<FeedViewModel,FeedEffects,FeedState>() {

    override val viewModel: FeedViewModel by viewModel()

    private val adapter by lazy {
        FeedAdapter(viewModel::itemClicked)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.feed_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retry_view.setOnClickListener { viewModel.loadContent() }
        feed_list.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        if(adapter.itemCount == 0) {
            viewModel.loadContent()
        }
    }

    companion object {
        fun launch(fragmentManager: FragmentManager) {
            val fragment = FeedFragment()

            fragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun render(state: FeedState) {
        loading_view.visibility = if(state.isLoading) View.VISIBLE else View.GONE
        retry_view.visibility = if(state.retryVisible) View.VISIBLE else View.GONE
        adapter.setData(state.content)
    }

    override fun handleSideEffect(effect: FeedEffects) {
        when(effect) {
            is FeedEffects.ViewContent -> {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(effect.url)
                }.also {
                    startActivity(it)
                }
            }
            is FeedEffects.ContentError -> {
                activity?.let { Snackbar.make(it.findViewById(R.id.container_coordinator),
                    R.string.feed_network_error, Snackbar.LENGTH_SHORT).show() }
            }
        }
    }
}
