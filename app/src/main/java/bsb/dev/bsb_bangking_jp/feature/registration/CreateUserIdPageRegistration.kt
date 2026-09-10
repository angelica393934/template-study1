package bsb.dev.bsb_bangking_jp.feature.registration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.component.AppTextField
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
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

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp),
        ) {
            Text(text = "Buat ID Pengguna Kamu", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "ID Pengguna ini akan digunakan untuk masuk ke akun dan mengakses Bank Sumsel Babel Mobile Banking.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            Spacer(modifier = Modifier.height(15.dp))

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
            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.extendedColors.divider)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Aturan ID Pengguna",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            Spacer(modifier = Modifier.height(10.dp))
            RuleBullet("Gunakan tepat 8 karakter", userId.isNotEmpty(), has8Chars)
            Spacer(modifier = Modifier.height(5.dp))
            RuleBullet("Gunakan kombinasi huruf besar dan kecil", userId.isNotEmpty(), hasUpperLower)
            Spacer(modifier = Modifier.height(5.dp))
            RuleBullet("Gunakan minimal satu angka", userId.isNotEmpty(), hasNumber)

            Spacer(modifier = Modifier.height(30.dp))

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
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

/** Reusable juga oleh CreateUserPwPageRegistration.kt (satu package yang sama). */
@Composable
internal fun RuleBullet(text: String, hasInput: Boolean, isValid: Boolean) {
    val color = when {
        !hasInput -> MaterialTheme.extendedColors.textDisabled
        isValid -> MaterialTheme.extendedColors.success
        else -> MaterialTheme.extendedColors.danger
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = color,
            modifier = Modifier.width(12.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = color)
    }
}