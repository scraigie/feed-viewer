package uk.co.simoncameron.feedviewer.domain

import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.co.simoncameron.feedviewer.data.preferences.AppPreferences
import uk.co.simoncameron.feedviewer.data.user.UserRepository
import uk.co.simoncameron.feedviewer.domain.auth.AuthenticationInteractor
import uk.co.simoncameron.feedviewer.domain.auth.AuthenticationState
import uk.co.simoncameron.feedviewer.domain.pojo.User


class AuthenticationInteractorImplTest {

    private val mockUserRepository: UserRepository = mock()
    private val mockAppPreferences: AppPreferences = mock()

    private lateinit var authenticationInteractor: AuthenticationInteractor.Impl
    
    @Before
    fun setUp() {
        authenticationInteractor = AuthenticationInteractor.Impl(mockUserRepository, mockAppPreferences)
    }

    @After
    fun tearDown() {
        reset(mockUserRepository)
    }

    @Test
    fun `authenticateUser returns AUTHENTICATED if user exists in the repository and password is valid`() {

        val username = "user_1"
        val password = "password"

        val domainUser = User(username, password)

        whenever(mockUserRepository.getUser(username)) doReturn Observable.just(domainUser)

        authenticationInteractor.authenticateUser(username, password).test()
            .assertValueCount(1)
            .assertNoErrors()
            .assertValue { it == AuthenticationState.AUTHORIZED }

        verify(mockAppPreferences).user = username
    }

    @Test
    fun `authenticateUser returns UNAUTHENTICATED if user exists in the repository but passwords do not match`() {

        val username = "user_1"
        val password = "password"

        val domainUser = User(username, "another password")

        whenever(mockUserRepository.getUser(username)) doReturn Observable.just(domainUser)

        authenticationInteractor.authenticateUser(username, password).test()
            .assertValueCount(1)
            .assertNoErrors()
            .assertValue { it == AuthenticationState.UNAUTHORIZED }
            .dispose()
    }

    @Test
    fun `authenticateUser returns UNAUTHENTICATED if an error is thrown because the user does not exist in the repository`() {

        val username = "user_1"
        val password = "password"

        whenever(mockUserRepository.getUser(username)) doReturn Observable.error(Throwable("User does not exist"))

        authenticationInteractor.authenticateUser(username, password).test()
            .assertValueCount(1)
            .assertNoErrors()
            .assertValue { it == AuthenticationState.UNAUTHORIZED }
            .dispose()
    }
}