package bsb.dev.bsb_bangking_jp.core.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun AppMenuHorizontal(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    @DrawableRes iconImg: Int? = null,
    scale: Float = 0.7f,
    showDivider: Boolean = true,
    onTap: (() -> Unit)? = null,
) {
    require(icon != null || iconImg != null) {
        "Harus isi salah satu: icon atau iconImg"
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = onTap != null) { onTap?.invoke() }
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    icon != null -> Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    iconImg != null -> Icon(
                        painter = painterResource(id = iconImg),
                        contentDescription = label,
                        modifier = Modifier.size(40.dp * scale),
                        // Unspecified -> pertahankan warna asli svg multi-warna
                        tint = Color.Unspecified,
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textPrimary,
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.textDisabled,
            )
        }

        if (showDivider) {
            HorizontalDivider(color = MaterialTheme.extendedColors.strip)
        }
    }
}