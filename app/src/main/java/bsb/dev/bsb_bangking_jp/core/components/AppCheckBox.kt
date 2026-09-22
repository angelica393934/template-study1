package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.Gray300
import bsb.dev.bsb_bangking_jp.core.theme.Orange300
import bsb.dev.bsb_bangking_jp.core.theme.Orange400
import bsb.dev.bsb_bangking_jp.core.theme.White

@Composable
fun AppCheckBox(
    value: Boolean,
    onChanged: ((Boolean) -> Unit)? = null,
    activeColor: Color? = null,
    borderRadius: Dp = 6.dp,
    padding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier,
) {
    val fillColor = activeColor ?: Orange300
    val borderColor = if (value) Orange400 else Gray300
    val shape = RoundedCornerShape(borderRadius)

    Box(
        modifier = modifier
            .padding(padding)
            .size(20.dp)
            .clip(shape)
            .background(if (value) fillColor else Color.Transparent)
            .border(width = 2.dp, color = borderColor, shape = shape)
            .toggleable(
                value = value,
                enabled = onChanged != null,
                role = Role.Checkbox,
                onValueChange = { onChanged?.invoke(it) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (value) {
            Canvas(modifier = Modifier.size(12.dp)) {
                val checkPath = Path().apply {
                    moveTo(size.width * 0.15f, size.height * 0.55f)
                    lineTo(size.width * 0.42f, size.height * 0.8f)
                    lineTo(size.width * 0.88f, size.height * 0.2f)
                }
                drawPath(
                    path = checkPath,
                    color = White,
                    style = Stroke(
                        width = 1.8.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }
    }
}