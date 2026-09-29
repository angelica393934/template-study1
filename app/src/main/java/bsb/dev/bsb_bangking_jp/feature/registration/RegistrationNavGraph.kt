package bsb.dev.bsb_bangking_jp.feature.registration

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.registrationNavGraph(navController: NavController) {
    navigation(startDestination = "registration_akun", route = "registration") {

        composable("registration_akun") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("registration") }
            val viewModel: RegistrationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            FindAccountPageRegistration(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToOtp = { navController.navigate("registration_otp") },
            )
        }

        composable("registration_otp") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("registration") }
            val viewModel: RegistrationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            OtpPageRegistration(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onVerified = { navController.navigate("registration_buat_id") },
            )
        }

        composable("registration_buat_id") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("registration") }
            val viewModel: RegistrationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            CreateUserIdPageRegistration(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToPasswordPage = { navController.navigate("registration_buat_password") },
            )
        }

        composable("registration_buat_password") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("registration") }
            val viewModel: RegistrationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            CreateUserPwPageRegistration(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onRegistrationSelesai = {
                    navController.navigate("portal") { popUpTo(0) }
                },
            )
        }
    }
}