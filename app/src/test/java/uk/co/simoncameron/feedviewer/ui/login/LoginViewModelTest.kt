package uk.co.simoncameron.feedviewer.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.co.simoncameron.feedviewer.domain.auth.AuthenticationInteractor
import uk.co.simoncameron.feedviewer.domain.auth.AuthenticationState


class LoginViewModelTest {

    lateinit var loginViewModel: LoginViewModel

    private val mockAuthenticationInteractor: AuthenticationInteractor = mock()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val stateObserverSpy = spy(Observer<LoginState> {  })
    private val effectsObserverSpy = spy(Observer<LoginEffects> {  })

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(mockAuthenticationInteractor)

        loginViewModel.sideEffectLiveData.observeForever(effectsObserverSpy)
        loginViewModel.stateLiveData.observeForever(stateObserverSpy)

        RxAndroidPlugins.setInitMainThreadSchedulerHandler { h -> Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { h -> Schedulers.trampoline() }
    }


    @After
    fun tearDown() {
        reset(mockAuthenticationInteractor)
        loginViewModel.stateLiveData.removeObserver(stateObserverSpy)
        loginViewModel.sideEffectLiveData.removeObserver(effectsObserverSpy)

        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun `on successful authentication a navigate to feed event is pushed`() {

        whenever(mockAuthenticationInteractor.authenticateUser(any(), any())) doReturn Observable.just(AuthenticationState.AUTHORIZED)

        loginViewModel.submit("username", "password")

        inOrder(stateObserverSpy, effectsObserverSpy) {
            verify(stateObserverSpy).onChanged(LoginState(isLoading = true))
            verify(effectsObserverSpy).onChanged(LoginEffects.NavigateToFeed)
            verify(stateObserverSpy).onChanged(LoginState(isLoading = false))
        }
    }

    @Test
    fun `on failed authentication a event to show an error is pushed`() {

        whenever(mockAuthenticationInteractor.authenticateUser(any(), any())) doReturn Observable.just(AuthenticationState.UNAUTHORIZED)

        loginViewModel.submit("username", "password")

        inOrder(stateObserverSpy, effectsObserverSpy) {
            verify(stateObserverSpy).onChanged(LoginState(isLoading = true))
            verify(effectsObserverSpy).onChanged(LoginEffects.ShowAuthError)
            verify(stateObserverSpy).onChanged(LoginState(isLoading = false))
        }
    }
}