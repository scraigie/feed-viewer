package uk.co.simoncameron.feedviewer.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "role") val role: UserRole
)

enum class UserRole(val dbCode: Int) {
    ROLE_PREMIUM(1),
    ROLE_NORMAL(0)
}