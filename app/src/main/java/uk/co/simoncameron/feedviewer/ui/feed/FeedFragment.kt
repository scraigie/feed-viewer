package uk.co.simoncameron.feedviewer.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import uk.co.simoncameron.feedviewer.R


class FeedFragment : Fragment() {

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
