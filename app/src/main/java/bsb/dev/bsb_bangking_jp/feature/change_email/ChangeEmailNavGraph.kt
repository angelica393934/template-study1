package bsb.dev.bsb_bangking_jp.feature.change_email

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import bsb.dev.bsb_bangking_jp.feature.change_email.presentation.ChangeEmailViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.changeEmailNavGraph(navController: NavController) {
    navigation(startDestination = "change_email_input", route = "change_email") {
        composable("change_email_input") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_email") }
            val viewModel: ChangeEmailViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            ChangeEmailPage(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToPin = { navController.navigate("change_email_pin") },
            )
        }
        composable("change_email_pin") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_email") }
            val viewModel: ChangeEmailViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            ChangeEmailPinPage(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onCompleted = {
                    // padanan 3x Navigator.pop() di ChangeEmailPage.dart -- buang seluruh
                    // stack alur change_email (input + pin), balik ke PengaturanPage.
                    navController.popBackStack("change_email", inclusive = true)
                },
            )
        }
    }
}