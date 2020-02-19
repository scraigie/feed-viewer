package uk.co.simoncameron.feedviewer.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import uk.co.simoncameron.feedviewer.R
import uk.co.simoncameron.feedviewer.ui.base.MviFragment


class FeedFragment : MviFragment<FeedViewModel,FeedEffects,FeedState>() {

    override val viewModel: FeedViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.feed_fragment, container, false)



    companion object {
        fun launch(fragmentManager: FragmentManager) {
            val fragment = FeedFragment()

            fragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(fragment.tag)
                .commit()
        }
    }
}
