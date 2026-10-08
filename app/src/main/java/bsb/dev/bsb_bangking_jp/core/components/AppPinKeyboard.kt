package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

private val KeyRows = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf("", "0", "back"),
)

private val KeyboardVerticalPadding = 10.dp
private val KeyboardRowSpacing = 4.dp
private val MinButtonSize = 48.dp
private val MaxButtonSize = 80.dp

/**
 * Hitung ukuran tombol dari tinggi yang boleh dipakai keyboard.
 * Dipakai InputPinPage supaya keyboard tidak memakan terlalu banyak tinggi layar.
 */
fun pinKeyboardButtonSize(availableHeight: Dp): Dp {
    val fixed = KeyboardVerticalPadding * 2 + KeyboardRowSpacing * KeyRows.size
    return ((availableHeight - fixed) / KeyRows.size).coerceIn(MinButtonSize, MaxButtonSize)
}

@Composable
fun AppPinKeyboard(
    onKeyTap: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp? = null, // null = otomatis dari tinggi/lebar yang tersedia
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // Batas dari lebar: 3 kolom, sisakan sedikit ruang antar tombol.
        val maxByWidth = (maxWidth - 48.dp) / 3
        val size = minOf(buttonSize ?: MaxButtonSize, maxByWidth)
            .coerceIn(MinButtonSize, MaxButtonSize)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = KeyboardVerticalPadding),
        ) {
            KeyRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    row.forEach { key ->
                        when {
                            key.isEmpty() -> Spacer(modifier = Modifier.size(size))
                            key == "back" -> BackKey(size = size, onClick = onBackspace)
                            else -> NumberKey(
                                label = key,
                                size = size,
                                onClick = { onKeyTap(key) },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(KeyboardRowSpacing))
            }
        }
    }
}

// NumberKey & BackKey tetap sama seperti sebelumnya
@Composable
private fun NumberKey(
    label: String,
    size: Dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(extendedColors.inputBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.displaySmall,
            color = extendedColors.textPrimary,
        )
    }
}

@Composable
private fun BackKey(
    size: Dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Backspace,
            contentDescription = "Hapus",
            tint = extendedColors.textPrimary,
        )
    }
}