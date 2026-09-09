package bsb.dev.bsb_bangking_jp.feature.transfer

import bsb.dev.bsb_bangking_jp.feature.transfer.daftar_bank.data.DaftarBankApiService
import bsb.dev.bsb_bangking_jp.feature.transfer.daftar_bank.data.DaftarBankLocalStore
import bsb.dev.bsb_bangking_jp.feature.transfer.daftar_bank.data.DaftarBankRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.transfer.last_transfer.data.LastTransferRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.data.SavedRecipientApiService
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.data.SavedRecipientRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.data.TransferApiService
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.data.TransferRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.transfer.daftar_bank.domain.DaftarBankRepository
import bsb.dev.bsb_bangking_jp.feature.transfer.last_transfer.domain.LastTransferRepository
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.domain.SavedRecipientRepository
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferRepository
import bsb.dev.bsb_bangking_jp.feature.transfer.daftar_bank.presentation.DaftarBankViewModel
import bsb.dev.bsb_bangking_jp.feature.transfer.last_transfer.presentation.LastTransferViewModel
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.presentation.SavedRecipientViewModel
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val transferModule = module {
    single { get<Retrofit>().create(DaftarBankApiService::class.java) }
    single { DaftarBankLocalStore(get()) }
    single<DaftarBankRepository> { DaftarBankRepositoryImpl(get(), get(), get()) }
    viewModel { DaftarBankViewModel(get()) }

    // -- flow transfer --
    single { get<Retrofit>().create(TransferApiService::class.java) }
    single<TransferRepository> { TransferRepositoryImpl(get(), get()) }
    // 🔹 single, BUKAN viewModel -- state harus bertahan lintas route
    // (transfer_baru -> transfer_bsb/umum -> pin_transfer)
    single { TransferViewModel(get(), get(),get (), get(),get()) }

    // -- flow saved recipient (daftar tersimpan) --
    single { get<Retrofit>().create(SavedRecipientApiService::class.java) }
    single<SavedRecipientRepository> { SavedRecipientRepositoryImpl(get(), get()) }
    viewModel { SavedRecipientViewModel(get()) }

    // -- flow LastTransfer (last Transfer) --
    single<LastTransferRepository> {
        LastTransferRepositoryImpl(
            apiHelper = get(),
            rekeningRepository = get()
        )
    }

    viewModel {
        LastTransferViewModel(
            repository = get()
        )
    }
}