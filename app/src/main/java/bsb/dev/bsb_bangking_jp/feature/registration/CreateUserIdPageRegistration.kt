package bsb.dev.bsb_bangking_jp.feature.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AdaptiveScrollColumn
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.components.RuleBullet
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationViewModel
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationNavEvent
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationUiEvent

@Composable
fun CreateUserIdPageRegistration(
    viewModel: RegistrationViewModel,
    onBackClick: () -> Unit,
    onNavigateToPasswordPage: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    var userId by remember { mutableStateOf("") }
    var confirmUserId by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf<String?>(null) }

    val has8Chars = userId.length == 8
    val hasUpperLower = Regex("(?=.*[a-z])(?=.*[A-Z])").containsMatchIn(userId)
    val hasNumber = Regex("[0-9]").containsMatchIn(userId)
    val isAllValid = has8Chars && hasUpperLower && hasNumber

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is RegistrationNavEvent.ToBuatPasswordPage) onNavigateToPasswordPage()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is RegistrationUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Buat ID Pengguna", onBackClick = onBackClick)
        AdaptiveScrollColumn(
            modifier = Modifier
                .weight(1f)
                .imePadding(),                       // supaya tidak tertutup keyboard
        ) {
        Column(
            modifier = Modifier
                .padding(all = appLayout.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs),
            ) {
            Text(text = "Buat ID Pengguna Kamu", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "ID Pengguna ini akan digunakan untuk masuk ke akun dan mengakses Bank Sumsel Babel Mobile Banking.",
                style = MaterialTheme.typography.bodyMedium,
                color = extendedColors.textSecondary,
            )
            AppTextField(
                value = userId,
                onValueChange = {
                    userId = it
                    viewModel.clearUserIdError()
                },
                labelText = "ID Pengguna Baru",
                hintText = "Masukkan ID Pengguna Baru",
                icon = Icons.Default.Person,
                errorText = uiState.userIdError,
                showError = uiState.userIdError != null,
                enableFocusBackground = true,
            )
            AppTextField(
                value = confirmUserId,
                onValueChange = {
                    confirmUserId = it
                    confirmError = null
                },
                labelText = "Ulangi ID Pengguna Baru",
                hintText = "Ulangi ID Pengguna Baru",
                icon = Icons.Default.Person,
                errorText = confirmError,
                showError = confirmError != null,
                enableFocusBackground = true,
            )
            Text(
                text = "Aturan ID Pengguna",
                style = MaterialTheme.typography.titleSmall,
                color = extendedColors.textSecondary,
            )
            RuleBullet("Gunakan tepat 8 karakter", userId.isNotEmpty(), has8Chars)
            RuleBullet(
                "Gunakan kombinasi huruf besar dan kecil",
                userId.isNotEmpty(),
                hasUpperLower
            )
            RuleBullet("Sertakan angka", userId.isNotEmpty(), hasNumber)

            Spacer(modifier = Modifier.height(appSpacing.ss))

            AppButton(
                text = "Lanjutkan",
                enabled = isAllValid,
                onClick = {
                    if (confirmUserId != userId) {
                        confirmError = "ID Pengguna baru tidak sama"
                        return@AppButton
                    }
                    confirmError = null
                    viewModel.addIdUser(userId, confirmUserId)
                },
            )
        }
        }
    }
}

