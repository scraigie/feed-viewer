package uk.co.simoncameron.feedviewer

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FeedViewerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@FeedViewerApp)
            modules(listOf(
                appModule,
                networkModule,
                dataModule))
        }
    }
}