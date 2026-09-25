package bsb.dev.bsb_bangking_jp.feature.notification

import bsb.dev.bsb_bangking_jp.feature.notification.data.NotifApiService
import bsb.dev.bsb_bangking_jp.feature.notification.data.NotifRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.notification.domain.NotifRepository
import bsb.dev.bsb_bangking_jp.feature.notification.presentation.NotifViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val notificationModule = module {
    single { get<Retrofit>().create(NotifApiService::class.java) }
    single<NotifRepository> { NotifRepositoryImpl(get(), get()) }
    viewModel { NotifViewModel(get()) }
}