package bsb.dev.bsb_bangking_jp.feature.transfer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun JumlahTransferField(
    amount: Int,
    onAmountChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Masukkan Jumlah Transfer",
    minAmount: Int? = null,
    maxAmount: Int? = null,
) {
    // 🔹 TextFieldValue, BUKAN String biasa -- supaya posisi kursor bisa dikontrol eksplisit.
    var fieldValue by remember(amount) {
        val text = if (amount == 0) "0" else formatRibuan(amount)
        mutableStateOf(TextFieldValue(text = text, selection = androidx.compose.ui.text.TextRange(text.length)))
    }

    val errorMessage = when {
        minAmount != null && amount in 1 until minAmount ->
            "Minimal transfer Rp ${formatRibuan(minAmount)}"
        maxAmount != null && amount > maxAmount ->
            "Maksimal transfer Rp ${formatRibuan(maxAmount)}"
        else -> null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Rp ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            BasicTextField(
                value = fieldValue,
                onValueChange = { newValue ->
                    val digits = newValue.text.filter { it.isDigit() }
                    val number = digits.toIntOrNull() ?: 0
                    val newText = if (number == 0) "0" else formatRibuan(number)

                    // 🔹 Kursor SELALU dipaksa ke akhir teks -- ini kunci perbaikannya.
                    fieldValue = TextFieldValue(
                        text = newText,
                        selection = androidx.compose.ui.text.TextRange(newText.length),
                    )
                    onAmountChange(number)
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.textPrimary,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
            )
            if (fieldValue.text != "0") {
                IconButton(onClick = {
                    fieldValue = TextFieldValue(text = "0", selection = androidx.compose.ui.text.TextRange(1))
                    onAmountChange(0)
                }) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.extendedColors.divider),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
        }

        HorizontalDivider(
            color = MaterialTheme.extendedColors.divider,
            thickness = 2.dp,
            modifier = Modifier.padding(top = 8.dp),
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extendedColors.danger,
            )
        }
    }
}

private fun formatRibuan(number: Int): String {
    return "%,d".format(number).replace(",", ".")
}