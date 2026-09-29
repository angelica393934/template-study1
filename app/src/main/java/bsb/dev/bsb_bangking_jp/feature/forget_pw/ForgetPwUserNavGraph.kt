package bsb.dev.bsb_bangking_jp.feature.forget_pw

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import bsb.dev.bsb_bangking_jp.feature.forget_pw.presentation.ForgetPwViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.forgetPwUserNavGraph(navController: NavController) {
    navigation(startDestination = "forget_pw_home", route = "forget_pw") {

        composable("forget_pw_home") { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) { navController.getBackStackEntry("forget_pw") }
            val viewModel: ForgetPwViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            FindAccountPageForgetPw(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToOtp = { navController.navigate("forget_pw_otp") },
            )
        }
        // forget pw user
        composable("forget_pw_otp") { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) { navController.getBackStackEntry("forget_pw") }
            val viewModel: ForgetPwViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            OtpPageForgetPw(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onVerified = { navController.navigate("forget_pw_reset") },
            )
        }

        composable("forget_pw_reset") { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) { navController.getBackStackEntry("forget_pw") }
            val viewModel: ForgetPwViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            ResetPwPageForgetPw(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onResetSuccessComplete = {
                    navController.navigate("portal") { popUpTo(0) }
                },
            )
        }
    }
}