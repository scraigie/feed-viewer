package uk.co.simoncameron.feedviewer.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import uk.co.simoncameron.feedviewer.R
import uk.co.simoncameron.feedviewer.ui.base.MviFragment
import uk.co.simoncameron.feedviewer.ui.feed.FeedFragment

class LoginFragment : MviFragment<LoginViewModel, LoginEffects, LoginState>() {

    override val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.login_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        login_button.setOnClickListener {
            val username = username.text ?: ""
            val password = password.text ?: ""
            viewModel.submit(username.toString(), password.toString()) }
    }

    override fun handleSideEffect(effect: LoginEffects) {
        when(effect){
            is LoginEffects.NavigateToFeed -> activity?.supportFragmentManager?.let { FeedFragment.launch(it) }
            is LoginEffects.ShowAuthError -> activity?.let { Snackbar.make(it.findViewById(R.id.container_coordinator),
                R.string.log_in_error, Snackbar.LENGTH_SHORT).show() }
        }
    }

    override fun render(state: LoginState) {
        loading_view.visibility = if (state.isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        fun launch(fragmentManager: FragmentManager) {
            val fragment = LoginFragment()

            fragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }
}
