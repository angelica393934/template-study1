package bsb.dev.bsb_bangking_jp.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun OtpPinInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    Box(modifier = modifier) {
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

        Row {
            repeat(length) { index ->
                val char = value.getOrNull(index)?.toString() ?: ""
                val isFocusedBox = index == value.length

                Box(
                    modifier = Modifier
                        .size(if (isFocusedBox) 53.dp else 45.dp)
                        .clip(RoundedCornerShape(if (isFocusedBox) 8.dp else 10.dp))
                        .border(
                            width = if (isFocusedBox) 2.dp else 1.dp,
                            color = when {
                                isError -> MaterialTheme.colorScheme.error
                                isFocusedBox -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.extendedColors.textDisabled
                            },
                            shape = RoundedCornerShape(if (isFocusedBox) 8.dp else 10.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = char,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                if (index != length - 1) {
                    androidx.compose.foundation.layout.Spacer(
                        modifier = Modifier.size(6.dp)
                    )
                }
            }
        }
    }
}