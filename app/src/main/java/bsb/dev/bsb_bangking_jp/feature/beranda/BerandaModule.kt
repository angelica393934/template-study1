package bsb.dev.bsb_bangking_jp.feature.beranda

import bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.data.BerandaApiService
import bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.data.GetBannerRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.domain.GetBannerRepository
import bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.presentation.BerandaViewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val BerandaModule = module {
    single { get<Retrofit>().create(BerandaApiService::class.java) }
    single<GetBannerRepository> { GetBannerRepositoryImpl(get(), get()) }

    // 🔹 single, BUKAN viewModel -- state banner tetap bertahan lintas navigasi.
    single { BerandaViewModel(get(), get(),  get()) }}