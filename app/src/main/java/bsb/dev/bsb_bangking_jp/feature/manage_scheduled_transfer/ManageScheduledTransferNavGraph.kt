package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.presentation.ScheduledTransferViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.manageScheduledTransferNavGraph(navController: NavController) {
    navigation(
        startDestination = "manage_scheduled_transfer_list",
        route = "manage_scheduled_transfer"
    ) {
        composable("manage_scheduled_transfer_list") { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) { navController.getBackStackEntry("manage_scheduled_transfer") }
            val viewModel: ScheduledTransferViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)

            ManageScheduledTransferPage(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onItemClick = { id -> navController.navigate("manage_scheduled_transfer_detail/$id") },
            )
        }

        composable(
            route = "manage_scheduled_transfer_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) { navController.getBackStackEntry("manage_scheduled_transfer") }
            val viewModel: ScheduledTransferViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable

            ScheduledTransferDetailPage(
                id = id,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}