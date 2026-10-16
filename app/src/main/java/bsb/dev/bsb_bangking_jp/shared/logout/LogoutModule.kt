package bsb.dev.bsb_bangking_jp.shared.logout

import bsb.dev.bsb_bangking_jp.shared.logout.data.LogoutApiService
import bsb.dev.bsb_bangking_jp.shared.logout.data.LogoutRepositoryImpl
import bsb.dev.bsb_bangking_jp.shared.logout.domain.LogoutRepository
import bsb.dev.bsb_bangking_jp.shared.logout.presentation.LogoutViewModel
import bsb.dev.bsb_bangking_jp.shared.session.SessionManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val logoutModule = module {
    single { get<Retrofit>().create(LogoutApiService::class.java) }
    single<LogoutRepository> { LogoutRepositoryImpl(get(), get()) }
    // 🔹 single, BUKAN factory -- satu instance dipakai di seluruh app,
    // padanan SessionManager  yang di-provide sekali ke seluruh tree.
    single { SessionManager(secureStorage = get(), sessionClearer = get()) }
    viewModel { LogoutViewModel(get(), get()) }
}