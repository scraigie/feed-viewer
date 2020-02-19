package uk.co.simoncameron.feedviewer

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import uk.co.simoncameron.feedviewer.data.api.ApiService
import uk.co.simoncameron.feedviewer.data.api.FeedDeserializer
import uk.co.simoncameron.feedviewer.data.db.AppDatabase
import uk.co.simoncameron.feedviewer.data.dto.FeedDTO
import uk.co.simoncameron.feedviewer.data.preferences.AppPreferences
import uk.co.simoncameron.feedviewer.data.user.UserRepository
import uk.co.simoncameron.feedviewer.domain.auth.AuthenticationInteractor
import uk.co.simoncameron.feedviewer.domain.feed.FeedInteractor
import uk.co.simoncameron.feedviewer.ui.feed.FeedViewModel
import uk.co.simoncameron.feedviewer.ui.login.LoginViewModel

val appModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { FeedViewModel(get()) }

    factory<AuthenticationInteractor> { AuthenticationInteractor.Impl(get(), get()) }
    factory<FeedInteractor> { FeedInteractor.Impl(get(), get(), get()) }
    factory<UserRepository> { UserRepository.Impl(get()) }
}

val networkModule = module {
    single<ApiService> {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder().registerTypeAdapter(FeedDTO::class.java, FeedDeserializer()).create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BuildConfig.API_URL)
            .build()
            .create(ApiService::class.java)
    }
}

val dataModule = module {
    factory<AppPreferences> { AppPreferences.Impl(get()) }
    single<SharedPreferences> { get<Context>().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE) }

    single { AppDatabase.getInstance(get()) }
    single { get<AppDatabase>().userDao() }
}
