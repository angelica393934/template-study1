package bsb.dev.bsb_bangking_jp.feature.forget_iduser

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import bsb.dev.bsb_bangking_jp.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppModalConfirm
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.components.RuleBullet
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserNavEvent
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserUiEvent
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetIdPageForgetId(
    viewModel: ForgetIdUserViewModel,
    onBackClick: () -> Unit,
    onResetSuccessComplete: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    var newUserId by remember { mutableStateOf("") }
    var confirmUserId by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf<String?>(null) }
    var showSuccessSheet by remember { mutableStateOf(false) }

    val has8Chars = newUserId.length == 8
    val hasUpperLower = Regex("(?=.*[a-z])(?=.*[A-Z])").containsMatchIn(newUserId)
    val hasNumber = Regex("[0-9]").containsMatchIn(newUserId)
    val isAllValid = has8Chars && hasUpperLower && hasNumber

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is ForgetIdUserNavEvent.ToPortalSuccess) showSuccessSheet = true
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is ForgetIdUserUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Ubah ID Pengguna", onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .padding(appLayout.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
            Image(
                painter = painterResource(id = R.drawable.asset_atur_id),
                contentDescription = null,
                modifier = Modifier.height(100.dp),
            )
            }
            Text(
                text = "Buat ID Pengguna baru untuk pengalaman masuk lebih nyaman.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )
            AppTextField(
                value = newUserId,
                onValueChange = {
                    newUserId = it
                    viewModel.clearNewUserIdError()
                },
                labelText = "ID Pengguna",
                hintText = "Masukkan ID Pengguna Baru",
                icon = Icons.Default.Person,
                errorText = uiState.newUserIdError,
                showError = uiState.newUserIdError != null,
                enableFocusBackground = true,
            )
            AppTextField(
                value = confirmUserId,
                onValueChange = {
                    confirmUserId = it
                    confirmError = null
                },
                labelText = "Ulangi ID Pengguna",
                hintText = "Ulangi ID Pengguna Baru",
                icon = Icons.Default.Person,
                errorText = confirmError,
                showError = confirmError != null,
                enableFocusBackground = true,
            )
            Text(
                text = "Aturan ID Pengguna",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.extendedColors.textSecondary,
                textAlign = TextAlign.Left,

                )
            RuleBullet("Gunakan maksimal 8 karakter", newUserId.isNotEmpty(), has8Chars)

            RuleBullet("Gunakan huruf besar dan kecil", newUserId.isNotEmpty(), hasUpperLower)

            RuleBullet("Sertakan angka", newUserId.isNotEmpty(), hasNumber)

            AppButton(
                text = "Lanjutkan",
                icon =  Icons.AutoMirrored.Filled.ArrowForward,
                enabled = isAllValid,
                modifier = Modifier.padding(vertical = appSpacing.xxxs),
                onClick = {
                    if (confirmUserId.isEmpty()) {
                        confirmError = "Ulangi ID pengguna tidak boleh kosong"
                        return@AppButton
                    }
                    if (confirmUserId != newUserId) {
                        confirmError = "ID pengguna baru tidak sama"
                        return@AppButton
                    }
                    confirmError = null
                    viewModel.changeIdUser(newUserId, confirmUserId)
                },
            )
        }
    }

    if (showSuccessSheet) {
        AppModalConfirm(
            onDismissRequest = {
                showSuccessSheet = false
                onResetSuccessComplete()
            },
            title = "ID Pengguna Berhasil di Perbarui",
            centerimage = R.drawable.asset_centang,
            description = "Silahkan gunakan id pengguna baru untuk menggunakan layanan kami.",
            confirmText = "Masuk Kembali",
            onConfirm = {
                showSuccessSheet = false
                onResetSuccessComplete()
            },
        )
    }
}