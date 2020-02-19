package uk.co.simoncameron.feedviewer.data.preferences

import android.content.SharedPreferences
import androidx.core.content.edit

interface AppPreferences {
    var user: String?

    class Impl(private val sharedPreferences: SharedPreferences): AppPreferences {

        override var user: String?
            get() = sharedPreferences.getString(CURRENT_USER_KEY, null)
            set(value) = sharedPreferences.edit { putString(CURRENT_USER_KEY, value) }

        companion object {
            private const val CURRENT_USER_KEY = "CURRENT_USER_KEY"
        }
    }
}
