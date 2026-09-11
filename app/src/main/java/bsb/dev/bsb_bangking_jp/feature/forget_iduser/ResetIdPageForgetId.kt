package bsb.dev.bsb_bangking_jp.feature.forget_iduser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.component.AppModalConfirm
import bsb.dev.bsb_bangking_jp.core.component.AppTextField
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserNavEvent
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserUiEvent
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserViewModel
import bsb.dev.bsb_bangking_jp.feature.registration.RuleBullet

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
        AppHeader(title = "Change User ID", onBackClick = onBackClick)

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Text(
                text = "Create a new User ID for a more comfortable sign-in experience.",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(20.dp))

            AppTextField(
                value = newUserId,
                onValueChange = {
                    newUserId = it
                    viewModel.clearNewUserIdError()
                },
                labelText = "New User ID",
                hintText = "Enter New User ID",
                icon = Icons.Default.Person,
                errorText = uiState.newUserIdError,
                showError = uiState.newUserIdError != null,
                enableFocusBackground = true,
            )
            Spacer(modifier = Modifier.height(10.dp))

            AppTextField(
                value = confirmUserId,
                onValueChange = {
                    confirmUserId = it
                    confirmError = null
                },
                labelText = "Confirm New User ID",
                hintText = "Repeat New User ID",
                icon = Icons.Default.Person,
                errorText = confirmError,
                showError = confirmError != null,
                enableFocusBackground = true,
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.extendedColors.divider)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "User ID Rules",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            Spacer(modifier = Modifier.height(10.dp))
            RuleBullet("Use exactly 8 characters", newUserId.isNotEmpty(), has8Chars)
            Spacer(modifier = Modifier.height(5.dp))
            RuleBullet("Use a mix of uppercase and lowercase letters", newUserId.isNotEmpty(), hasUpperLower)
            Spacer(modifier = Modifier.height(5.dp))
            RuleBullet("Use at least one number", newUserId.isNotEmpty(), hasNumber)

            Spacer(modifier = Modifier.height(30.dp))

            AppButton(
                text = "Continue",
                enabled = isAllValid,
                onClick = {
                    if (confirmUserId.isEmpty()) {
                        confirmError = "Confirmation User ID cannot be empty"
                        return@AppButton
                    }
                    if (confirmUserId != newUserId) {
                        confirmError = "User ID does not match"
                        return@AppButton
                    }
                    confirmError = null
                    viewModel.changeIdUser(newUserId, confirmUserId)
                },
            )
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showSuccessSheet) {
        AppModalConfirm(
            onDismissRequest = {
                showSuccessSheet = false
                onResetSuccessComplete()
            },
            title = "User ID Updated Successfully",
            description = "Please use your new User ID to access our services.",
            confirmText = "Back to Sign In",
            onConfirm = {
                showSuccessSheet = false
                onResetSuccessComplete()
            },
        )
    }
}