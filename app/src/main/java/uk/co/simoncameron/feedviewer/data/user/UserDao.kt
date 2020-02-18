package uk.co.simoncameron.feedviewer.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Observable
import uk.co.simoncameron.feedviewer.data.db.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE username=:username")
    fun getUser(username: String): Observable<List<UserEntity>>

    @Insert
    fun insertAll(vararg users: UserEntity)
}