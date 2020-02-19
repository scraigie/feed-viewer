package uk.co.simoncameron.feedviewer.data.user

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import uk.co.simoncameron.feedviewer.data.db.UserEntity
import uk.co.simoncameron.feedviewer.domain.pojo.User

interface UserRepository {
    fun getUser(username: String): Observable<User>

    class Impl(private val userDao: UserDao): UserRepository {
        override fun getUser(username: String): Observable<User> {
            return userDao.getUser(username)
                .subscribeOn(Schedulers.io())
//                .delay(1, TimeUnit.SECONDS) //To show loading spinner state
                .switchMap {
                    if(it.isEmpty()) {
                        Observable.error(Throwable("User not found in db"))
                    } else {
                        Observable.just(it.first())
                    }
                }
                .map { it.mapToDomain() }
        }


    }

    fun UserEntity.mapToDomain() = User(
        username = username,
        password = password,
        role = role
    )
}

