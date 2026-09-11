package bsb.dev.bsb_bangking_jp.feature.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.component.SelectableOptionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotAccountBottomSheet(
    onDismiss: () -> Unit,
    onSelectUserId: () -> Unit,
    onSelectPassword: () -> Unit,
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    AppModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SelectableOptionCard(
                title = "User ID",
                description = "Reset your account's User ID to restore access and keep your profile secure.",
                isSelected = selectedOption == "user_id",
                onTap = { selectedOption = "user_id" },
            )
            Spacer(modifier = Modifier.height(10.dp))
            SelectableOptionCard(
                title = "Password",
                description = "Reset your account's password to restore access and protect your data.",
                isSelected = selectedOption == "password",
                onTap = { selectedOption = "password" },
            )
            Spacer(modifier = Modifier.height(15.dp))

            AppButton(
                text = "Continue",
                enabled = selectedOption != null,
                onClick = {
                    when (selectedOption) {
                        "user_id" -> onSelectUserId()
                        "password" -> onSelectPassword()
                    }
                },
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}