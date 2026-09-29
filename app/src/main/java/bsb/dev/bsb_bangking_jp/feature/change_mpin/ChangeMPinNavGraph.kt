package bsb.dev.bsb_bangking_jp.feature.change_mpin

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import bsb.dev.bsb_bangking_jp.feature.change_email.ChangeEmailPage
import bsb.dev.bsb_bangking_jp.feature.change_email.ChangeEmailPinPage
import bsb.dev.bsb_bangking_jp.feature.change_email.presentation.ChangeEmailViewModel
import bsb.dev.bsb_bangking_jp.feature.change_mpin.presentation.ChangeMpinViewModel
import bsb.dev.bsb_bangking_jp.feature.change_pw.NewPasswordPage
import bsb.dev.bsb_bangking_jp.feature.change_pw.OldPasswordPage
import bsb.dev.bsb_bangking_jp.feature.change_pw.OtpChangePwPage
import bsb.dev.bsb_bangking_jp.feature.change_pw.presentation.ChangePwViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.changeMPinNavGraph(navController: NavController) {
    navigation(startDestination = "change_mpin_flow", route = "change_mpin") {
        composable("change_mpin_flow") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_mpin") }
            val viewModel: ChangeMpinViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            ChangeMpinFlow(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToOtp = { navController.navigate("change_mpin_otp") },
            )
        }
        composable("change_mpin_otp") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_mpin") }
            val viewModel: ChangeMpinViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            OtpChangeMpinPage(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onCompleted = {
                    // 🔹 AppNavigation cuma tahu SATU hal: ke mana harus pindah setelah selesai.
                    navController.popBackStack("navbar", inclusive = false)
                },
            )
        }
    }
}