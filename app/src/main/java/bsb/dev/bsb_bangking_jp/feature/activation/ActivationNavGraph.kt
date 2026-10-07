package bsb.dev.bsb_bangking_jp.feature.activation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import bsb.dev.bsb_bangking_jp.feature.activation.presentation.ActivationViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.activationNavGraph(navController: NavController) {
    navigation(startDestination = "aktivasi_akun", route = "activation") {
        composable("aktivasi_akun") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("activation") }
            val viewModel: ActivationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            FindAccountPageActivation(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToIdPengguna = { navController.navigate("aktivasi_id") },
            )
        }

        composable("aktivasi_id") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("activation") }
            val viewModel: ActivationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            InputIdPageActivation(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToOtp = { navController.navigate("aktivasi_otp") },
            )
        }

        composable("aktivasi_otp") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("activation") }
            val viewModel: ActivationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            OtpPageActivation(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onVerified = { navController.navigate("aktivasi_password") },
            )
        }

        composable("aktivasi_password") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("activation") }
            val viewModel: ActivationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            InputPwPageActivation(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToPin = { navController.navigate("aktivasi_pin") },
            )
        }

        composable("aktivasi_pin") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("activation") }
            val viewModel: ActivationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            ActivationPinFlow(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onCompleted = {
                    navController.navigate("portal") { popUpTo(0) }
                },
            )
        }
    }
}