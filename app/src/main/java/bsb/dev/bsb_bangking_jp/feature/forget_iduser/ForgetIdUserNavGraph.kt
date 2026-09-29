package bsb.dev.bsb_bangking_jp.feature.forget_iduser

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.forgetIdUserNavGraph(navController: NavController) {
    navigation(startDestination = "forget_iduser_home", route = "forget_iduser") {

        composable("forget_iduser_home") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("forget_iduser") }
            val viewModel: ForgetIdUserViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            FindAccountPageForgetId(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToOtp = { navController.navigate("forget_iduser_otp") },
            )
        }

        composable("forget_iduser_otp") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("forget_iduser") }
            val viewModel: ForgetIdUserViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            OtpPageForgetId(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onVerified = { navController.navigate("forget_iduser_reset") },
            )
        }

        composable("forget_iduser_reset") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("forget_iduser") }
            val viewModel: ForgetIdUserViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            ResetIdPageForgetId(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onResetSuccessComplete = {
                    navController.navigate("portal") { popUpTo(0) }
                },
            )
        }
    }
}