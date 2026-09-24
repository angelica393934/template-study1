package bsb.dev.bsb_bangking_jp.feature.pengaturan

import bsb.dev.bsb_bangking_jp.feature.pengaturan.set_photo_profile.data.SetPhotoProfileApiService
import bsb.dev.bsb_bangking_jp.feature.pengaturan.set_photo_profile.data.SetPhotoProfileRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.pengaturan.set_photo_profile.domain.SetPhotoProfileRepository
import bsb.dev.bsb_bangking_jp.feature.pengaturan.set_photo_profile.presentation.PhotoProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val pengaturanModule = module {
    single { get<Retrofit>().create(SetPhotoProfileApiService::class.java) }
    single<SetPhotoProfileRepository> { SetPhotoProfileRepositoryImpl(get(), get(), get()) }

    // viewModel biasa -- state edit foto tidak perlu bertahan lintas navigasi
    viewModel { PhotoProfileViewModel(get()) }
}