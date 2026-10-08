package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun OtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    enabled: Boolean = true,
    isError: Boolean = false,
    spacing: Dp = 8.dp,
    maxBoxSize: Dp = 64.dp, // batas atas supaya di tablet tidak kegedean
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // Ukuran kotak = (lebar tersedia - total jarak antar kotak) / jumlah kotak
        val boxSize = ((maxWidth - spacing * (length - 1)) / length)
            .coerceAtMost(maxBoxSize)

        // Field asli disembunyikan, cuma buat handle input & fokus keyboard.
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isDigit() }.take(length)
                onValueChange(filtered)
            },
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier
                .size(1.dp)
                .background(Color.Transparent),
            decorationBox = { }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(length) { index ->
                val char = value.getOrNull(index)?.toString() ?: ""
                val isFocusedBox = index == value.length
                val shape = RoundedCornerShape(10.dp)

                Box(
                    modifier = Modifier
                        .size(boxSize) // persegi & sama untuk semua kotak
                        .clip(shape)
                        .border(
                            width = if (isFocusedBox) 2.dp else 1.dp,
                            color = when {
                                isError -> MaterialTheme.colorScheme.error
                                isFocusedBox -> MaterialTheme.colorScheme.primary
                                else -> extendedColors.textDisabled
                            },
                            shape = shape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = char,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        }
    }
}