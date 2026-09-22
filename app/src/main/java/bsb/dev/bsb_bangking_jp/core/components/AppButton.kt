package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,

    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.extendedColors.onSuccess,

    outlined: Boolean = false,
    enabled: Boolean = true,

    icon: ImageVector? = null,
    iconBeforeText: Boolean = false,

    smallWidth: Boolean = false
) {

    val buttonModifier =
        if (smallWidth)
            modifier
                .wrapContentWidth()
                .height(40.dp)
        else
            modifier
                .fillMaxWidth()
                .height(50.dp)

    val content: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (iconBeforeText && icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor
                )
                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = text,
                style = textStyle,
                color = textColor
            )

            if (!iconBeforeText && icon != null) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor
                )
            }
        }
    }

    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = buttonModifier,
            shape = RoundedCornerShape(100.dp),
            border = BorderStroke(
                1.dp,
                backgroundColor
            )
        ) {
            content()
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = buttonModifier,
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = textColor
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp
            )
        ) {
            content()
        }
    }
}