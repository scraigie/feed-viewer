package uk.co.simoncameron.feedviewer.data.preferences

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.*
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class AppPreferencesImplTest {

    private lateinit var appPreferences: AppPreferences.Impl

    private val sharedPreferences: SharedPreferences = mock {
        whenever(it.edit()) doReturn mock()
    }

    @Before
    fun setUp() {
        appPreferences = AppPreferences.Impl(sharedPreferences)
    }

    @After
    fun tearDown() {
        reset(sharedPreferences)
    }

    @Test
    fun `setting user commits to shared preferences`() {

        val username = "username"

        appPreferences.user = username

        inOrder(sharedPreferences, sharedPreferences.edit()) {
            verify(sharedPreferences).edit()
            verify(sharedPreferences.edit()).putString(any(), eq(username))
            verify(sharedPreferences.edit()).apply()
        }
    }

    @Test
    fun `get user pulls value from shared preferences`() {

        val storedUsername = "username"

        whenever(sharedPreferences.getString(any(),eq(null))).doReturn(storedUsername)

        val username = appPreferences.user

        verify(sharedPreferences).getString(any(), eq(null))

        assertThat(username, equalTo(storedUsername))
    }

}