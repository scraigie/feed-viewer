package uk.co.simoncameron.feedviewer.domain.pojo

import uk.co.simoncameron.feedviewer.data.db.UserRole

data class User(
    val username: String,
    val password: String,
    val role: UserRole
)