package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    message: String = "Tidak ada hasil yang sesuai.",
    subMessage: String = "Gunakan kata kunci atau filter berbeda.",
    actionText: String? = "Coba Lagi",
    smallWidth: Boolean = true,
    onAction: (() -> Unit)? = null,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        // AUTO DETECT COMPACT
        val isCompact = maxHeight < 500.dp

        val imageSize = if (isCompact) 120.dp else 150.dp
        val spacingLarge = if (isCompact) 8.dp else 10.dp
        val spacingSmall = if (isCompact) 3.dp else 5.dp

        val titleStyle: TextStyle = if (isCompact) {
            MaterialTheme.typography.titleMedium
        } else {
            MaterialTheme.typography.titleLarge
        }

        val subStyle: TextStyle = if (isCompact) {
            MaterialTheme.typography.bodySmall
        } else {
            MaterialTheme.typography.bodyMedium
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.not_found),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(imageSize)
            )

            Spacer(
                modifier = Modifier.padding(top = spacingLarge)
            )

            Text(
                text = message,
                textAlign = TextAlign.Center,
                style = titleStyle,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.padding(top = spacingSmall)
            )

            Text(
                text = subMessage,
                textAlign = TextAlign.Center,
                style = subStyle,
                modifier = Modifier.fillMaxWidth()
            )

            // BUTTON OPTIONAL
            if (onAction != null && actionText != null) {
                Spacer(
                    modifier = Modifier.padding(top = spacingLarge)
                )

                AppButton(
                    text = actionText,
                    onClick = onAction,
                    smallWidth = smallWidth,
                )
            }
        }
    }
}