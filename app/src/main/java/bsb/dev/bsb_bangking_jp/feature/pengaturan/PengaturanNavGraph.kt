package bsb.dev.bsb_bangking_jp.feature.pengaturan

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import bsb.dev.bsb_bangking_jp.feature.lokasi_atm.LokasiAtmPage

fun NavGraphBuilder.pengaturanNavGraph(navController: NavController) {
    composable("lokasiatm") {
        LokasiAtmPage(
            navController = navController,
            onBack = {
                navController.popBackStack()
            },
        )
    }
    composable("faq") {
        FaqPage(onBackClick = { navController.popBackStack() })
    }
    composable("syarat_ketentuan") {
        SyaratKetentuanPage(onBackClick = { navController.popBackStack() })
    }
    composable("tentang_aplikasi") {
        TentangAplikasiPage(onBackClick = { navController.popBackStack() })
    }
}