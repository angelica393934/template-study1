package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun BottomSheetIndicator(
    modifier: Modifier = Modifier,
    height: Dp = 4.dp,
    closeAll: Boolean = false,
    onClose: (() -> Unit)? = null,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding( top=6.dp),
    ) {
        // Garis drag handle, center horizontal
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 18.dp)
                .width(screenWidth * 0.2f)
                .height(height)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.outline), // padanan gray400 (Theme.kt: outline = Gray400)
        )

        // Tombol close (X), pojok kanan atas
        IconButton(
            onClick = { onClose?.invoke() },
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(end = 20.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Tutup",
                tint = MaterialTheme.extendedColors.textPrimary, // padanan gray950
            )
        }
    }
}