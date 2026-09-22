package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.theme.Gray300
import bsb.dev.bsb_bangking_jp.core.theme.Primary2

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hintText: String = stringResource(R.string.lainnya_search_hint),
    modifier: Modifier = Modifier,
    height: Dp = 34.dp
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium,
        cursorBrush = SolidColor(Primary2),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .border(
                width = 1.dp,
                color = Gray300,
                shape = RoundedCornerShape(100.dp)
            ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 15.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari",
                    tint = Gray300,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(18.dp)
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = hintText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray300
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}
