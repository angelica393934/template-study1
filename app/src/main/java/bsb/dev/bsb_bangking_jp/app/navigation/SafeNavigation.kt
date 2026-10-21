package bsb.dev.bsb_bangking_jp.app.navigation

import androidx.navigation.NavController

/**
 * Padanan onUnknownRoute di Flutter.
 * - route null            -> halaman belum ditentukan -> UnderConstruction
 * - route tidak terdaftar -> navigate() melempar IllegalArgumentException -> UnderConstruction
 */
fun NavController.navigateOrUnderConstruction(route: String?) {
    if (route.isNullOrBlank()) {
        navigateToUnderConstruction()
        return
    }
    try {
        navigate(route)
    } catch (e: IllegalArgumentException) {
        navigateToUnderConstruction()
    }
}

fun NavController.navigateToUnderConstruction() {
    navigate("under_construction") { launchSingleTop = true }
}