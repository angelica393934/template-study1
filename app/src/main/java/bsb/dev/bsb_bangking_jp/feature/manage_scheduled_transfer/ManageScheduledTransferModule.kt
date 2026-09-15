package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer

import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain.ScheduledTransferRepository
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.data.ScheduledTransferApiService
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.data.ScheduledTransferRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.presentation.ScheduledTransferViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val manageScheduledTransferModule = module {
    single { get<Retrofit>().create(ScheduledTransferApiService::class.java) }
    single<ScheduledTransferRepository> { ScheduledTransferRepositoryImpl(get(), get()) }

    // viewModel biasa, di-scope ke nav graph "manage_scheduled_transfer" via parentEntry
    // (lihat AppNavigation) supaya List page & Detail page berbagi 1 instance.
    viewModel { ScheduledTransferViewModel(get()) }
}