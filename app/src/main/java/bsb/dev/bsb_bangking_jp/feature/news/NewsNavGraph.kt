package bsb.dev.bsb_bangking_jp.feature.news

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.newsNavGraph (navController: NavController) {
    composable("berita_list") {
        AllNewsPage(
            navController = navController,
            onBeritaClick = { item ->
                navController.navigate("berita_detail/${item.id}")
            },
            onBackClick = { navController.popBackStack() },
        )
    }

    composable(
        route = "berita_detail/{newsId}",
        arguments = listOf(navArgument("newsId") { type = NavType.IntType }),
    ) { backStackEntry ->
        val newsId = backStackEntry.arguments?.getInt("newsId") ?: 0
        NewsDetailPage(
            newsId = newsId,
            onBackClick = { navController.popBackStack() },
        )
    }
}