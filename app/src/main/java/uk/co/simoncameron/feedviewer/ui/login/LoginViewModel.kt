package uk.co.simoncameron.feedviewer.ui.login

import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import uk.co.simoncameron.feedviewer.domain.auth.AuthenticationInteractor
import uk.co.simoncameron.feedviewer.domain.auth.AuthenticationState
import uk.co.simoncameron.feedviewer.ui.base.*
import uk.co.simoncameron.feedviewer.ui.login.LoginState.Companion.DEFAULT_STATE

data class LoginState(val isLoading: Boolean) : State {
    companion object {
        val DEFAULT_STATE = LoginState( isLoading = false)
    }
}

sealed class LoginActions : Action {
    data class LoginClicked(
        val username: String,
        val password: String) : LoginActions()
}

sealed class LoginEffects: Effect {
    object NavigateToFeed: LoginEffects()
    object ShowAuthError: LoginEffects()
}

object LoginReducers {
    fun loading(): Reducer<LoginState> = { previous: LoginState -> previous.copy(isLoading = true) }
    fun authenticated(): Reducer<LoginState> = { previous: LoginState -> previous.copy(isLoading = false) }
}

class LoginViewModel(private val authenticationInteractor: AuthenticationInteractor)
    : MviViewModel<LoginActions, LoginEffects, LoginState>(DEFAULT_STATE) {

    override val actionProcessor = ObservableTransformer<LoginActions,Reducer<LoginState>> { upstream ->
        upstream.publish { connectibleUpstream ->
            // No real need to multicast due to single action only
            connectibleUpstream.ofType(LoginActions.LoginClicked::class.java)
                .switchMap {
                    authenticationInteractor.authenticateUser(
                        username = it.username,
                        password = it.password
                    )
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext {
                            sideEffectEvent.value = when(it) {
                                AuthenticationState.AUTHORIZED -> LoginEffects.NavigateToFeed
                                AuthenticationState.UNAUTHORIZED -> LoginEffects.ShowAuthError
                            }
                        }
                        .map { LoginReducers.authenticated() }
                        .startWith(LoginReducers.loading())
                }
                .doOnNext{ println(it) }
        }
    }

    fun submit(username: String, password: String) {
        actionsSubject.onNext(LoginActions.LoginClicked(username,password))
    }
}
