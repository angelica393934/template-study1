package bsb.dev.bsb_bangking_jp.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppModalConfirm(
    onDismissRequest: (() -> Unit),
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    @DrawableRes topimage: Int? = null,
    @DrawableRes centerimage: Int? = null,
    modifier_image: Modifier = Modifier.height(110.dp).fillMaxSize(),
    title: String? = null,
    description: String? = null,
    cancelText: String? = null,
    onCancel: (() -> Unit)? = null,
    confirmText: String? = null,
    onConfirm: (() -> Unit)? = null,
    onClose: (() -> Unit)? = onDismissRequest,
) {
    AppModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        onClose = onClose,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Gambar (atas sebelum judul opsional)
            topimage?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = modifier_image
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Title (opsional)
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            centerimage?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = modifier_image
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Description (opsional)
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extendedColors.textSecondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Row tombol -- fleksibel, bisa cuma 1 atau keduanya.
            val showCancel = cancelText != null && onCancel != null
            val showConfirm = confirmText != null && onConfirm != null

            if (showCancel || showConfirm) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (showCancel) {
                        AppButton(
                            text = cancelText,
                            onClick = onCancel,
                            backgroundColor = MaterialTheme.colorScheme.inverseSurface,
                            textColor = MaterialTheme.colorScheme.inverseOnSurface,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (showCancel && showConfirm) {
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    if (showConfirm) {
                        AppButton(
                            text = confirmText,
                            onClick = onConfirm,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}