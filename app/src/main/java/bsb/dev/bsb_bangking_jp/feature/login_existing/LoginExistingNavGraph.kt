package bsb.dev.bsb_bangking_jp.feature.login_existing

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.loginExistingNavGraph(navController: NavController) {
    navigation(startDestination = "login_masuk", route = "login_existing") {

        composable("login_masuk") { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) { navController.getBackStackEntry("login_existing") }
            val viewModel: LoginExistingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            MasukPage(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToOtp = { navController.navigate("login_otp") },
            )
        }

        composable("login_otp") { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) { navController.getBackStackEntry("login_existing") }
            val viewModel: LoginExistingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
            OtpMasukAkunPage(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onVerified = { navController.navigate("login_pin") },
            )
        }

        composable("login_pin") { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) { navController.getBackStackEntry("login_existing") }
            val viewModel: LoginExistingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            MasukPinFlow(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onCompleted = {
                    navController.navigate("portal") {
                        popUpTo("login_existing") { inclusive = true }
                    }
                },
            )
        }
    }
}