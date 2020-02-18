package uk.co.simoncameron.feedviewer.domain.auth

import io.reactivex.Observable
import uk.co.simoncameron.feedviewer.data.preferences.AppPreferences
import uk.co.simoncameron.feedviewer.data.user.UserRepository

interface AuthenticationInteractor {

    fun authenticateUser(username: String, password: String) : Observable<AuthenticationState>

    class Impl(private val userRepository: UserRepository,
               private val appPreferences: AppPreferences):
        AuthenticationInteractor {

        override fun authenticateUser(username: String, password: String): Observable<AuthenticationState> {

            return userRepository.getUser(username)
                .map {
                    if(it.password == password) {
                        AuthenticationState.AUTHORIZED
                    } else {
                        AuthenticationState.UNAUTHORIZED
                    }
                }
                .doOnNext { if(it == AuthenticationState.AUTHORIZED) appPreferences.user = username }
                .onErrorReturn { AuthenticationState.UNAUTHORIZED }
        }

    }
}