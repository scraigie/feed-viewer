package uk.co.simoncameron.feedviewer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uk.co.simoncameron.feedviewer.R
import uk.co.simoncameron.feedviewer.ui.login.LoginFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.let {
                LoginFragment.launch(it)
            }
        }
    }

    override fun onBackPressed() {
        if(supportFragmentManager.popBackStackImmediate()) {
            return
        }

        super.onBackPressed()
    }

}
