package bsb.dev.bsb_bangking_jp.feature.change_pw

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import bsb.dev.bsb_bangking_jp.feature.change_pw.presentation.ChangePwViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.changePwNavGraph(navController: NavController) {
    navigation(startDestination = "change_pw_old", route = "change_pw") {

        composable("change_pw_old") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_pw") }
            val viewModel: ChangePwViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            OldPasswordPage(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToNewPassword = { navController.navigate("change_pw_new") },
            )
        }

        composable("change_pw_new") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_pw") }
            val viewModel: ChangePwViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            NewPasswordPage(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToOtp = { navController.navigate("change_pw_otp") },
            )
        }

        composable("change_pw_otp") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_pw") }
            val viewModel: ChangePwViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            OtpChangePwPage(
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