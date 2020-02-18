package uk.co.simoncameron.feedviewer

import android.content.Context
import android.content.SharedPreferences
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import uk.co.simoncameron.feedviewer.data.db.AppDatabase
import uk.co.simoncameron.feedviewer.data.preferences.AppPreferences
import uk.co.simoncameron.feedviewer.data.user.UserRepository
import uk.co.simoncameron.feedviewer.domain.auth.AuthenticationInteractor
import uk.co.simoncameron.feedviewer.ui.login.LoginViewModel

val appModule = module {
    viewModel { LoginViewModel(get()) }
    factory<AuthenticationInteractor> { AuthenticationInteractor.Impl(get(), get()) }
    factory<UserRepository> { UserRepository.Impl(get()) }
}

val networkModule = module {
}

val dataModule = module {
    factory<AppPreferences> { AppPreferences.Impl(get()) }
    single<SharedPreferences> { get<Context>().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE) }

    single { AppDatabase.getInstance(get()) }
    single { get<AppDatabase>().userDao() }
}
