package uk.co.simoncameron.feedviewer.data.user

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.co.simoncameron.feedviewer.data.db.UserEntity
import uk.co.simoncameron.feedviewer.data.db.UserRole
import uk.co.simoncameron.feedviewer.domain.pojo.User
import uk.co.simoncameron.feedviewer.testutils.TestSchedulerRule

class UserRepositoryImplTest {

    private lateinit var userRepository: UserRepository.Impl

    private val userDao: UserDao = mock()

    @Rule
    @JvmField
    val testSchedulerRule = TestSchedulerRule()

    @Before
    fun setUp() {
        userRepository = UserRepository.Impl(userDao)

        RxJavaPlugins.setIoSchedulerHandler { h -> Schedulers.trampoline() }

    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    @Test
    fun `getUser will return the user from the db if it exists`() {
        val username = "username"
        val password = "password"

        whenever(userDao.getUser(username)) doReturn  Observable.just(listOf(UserEntity(username,password, UserRole.ROLE_NORMAL)))

        userRepository.getUser(username).test()
            .assertValueCount(1)
            .assertValue(User(
                        username = username,
                        password = password,
                        role = UserRole.ROLE_NORMAL)
            )
            .dispose()
    }

    @Test
    fun `getUser will return an error if user does not exist in the db`() {
        val username = "username"

        val error = Throwable("User does not exist")

        whenever(userDao.getUser(username)) doReturn Observable.error(error)

        userRepository.getUser(username).test()
            .assertValueCount(0)
            .assertError(error)
            .dispose()
    }
}