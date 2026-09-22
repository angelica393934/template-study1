package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,

    hintText: String? = null,
    labelText: String? = null,
    headerLabel: String? = null,
    leftDisplayText: String? = null,
    rightDisplayText: String? = null,

    obscureText: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    icon: ImageVector? = null,
    isDropdown: Boolean = false,
    onClick: (() -> Unit)? = null,
    readOnly: Boolean = false,
    suffixIcon: (@Composable (() -> Unit))? = null,
    isNumberOnly: Boolean = false,
    maxLength: Int? = null,
    errorText: String? = null,
    showError: Boolean = false,
    onClearError: (() -> Unit)? = null,
    enableFocusBackground: Boolean = false,
    useControllerAsLeftDisplayText: Boolean = false,
    isRightTextGreen: Boolean = false,
    height: Dp = 50.dp,
    BoldInputStyle: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    var isHidden by remember { mutableStateOf(obscureText) }

    val effectiveKeyboardType = if (isNumberOnly) KeyboardType.Number else keyboardType
    val isReadOnlyField = readOnly || isDropdown

    val iconColor = when {
        showError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val borderColor = when {
        showError -> MaterialTheme.colorScheme.error
        isReadOnlyField && !isDropdown -> MaterialTheme.extendedColors.textDisabled
        isFocused -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.extendedColors.textDisabled
    }

    val backgroundColor: Color = when {
        !enableFocusBackground -> Color.Transparent
        readOnly && !isDropdown -> MaterialTheme.colorScheme.onPrimary
        isFocused -> Color.White
        value.isNotEmpty() -> Color.White
        else -> MaterialTheme.colorScheme.onPrimary
    }

    val resolvedLeftDisplayText = leftDisplayText
        ?: if (useControllerAsLeftDisplayText && value.isNotEmpty()) value else null

    val cursorColor = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    val InputStyle =
        if (BoldInputStyle) {
            MaterialTheme.typography.titleMedium
        } else {
            MaterialTheme.typography.bodyMedium
        }

    Column(modifier = modifier.fillMaxWidth()) {

        headerLabel?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
            )
        }

        labelText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = if (showError) MaterialTheme.colorScheme.error
                else MaterialTheme.extendedColors.textPrimary,
                modifier = Modifier.padding(start = 20.dp, top = 4.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Box(modifier = Modifier.fillMaxWidth()) {

            BasicTextField(
                value = value,
                onValueChange = { newValue ->
                    var filtered = newValue
                    if (isNumberOnly) filtered = filtered.filter { it.isDigit() }
                    if (maxLength != null && filtered.length > maxLength) {
                        filtered = filtered.take(maxLength)
                    }
                    onValueChange(filtered)
                    if (showError && filtered.isNotEmpty()) {
                        onClearError?.invoke()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .background(color = backgroundColor, shape = RoundedCornerShape(100.dp))
                    .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(100.dp)),
                interactionSource = interactionSource,
                readOnly = isReadOnlyField,
                singleLine = true,
                enabled = true,
                textStyle = InputStyle.copy(
                    color = MaterialTheme.extendedColors.textPrimary
                ),
                cursorBrush = SolidColor(cursorColor),
                visualTransformation =
                    if (obscureText && isHidden) PasswordVisualTransformation()
                    else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = effectiveKeyboardType),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ---- Leading slot ----
                        when {
                            resolvedLeftDisplayText != null -> {
                                Text(
                                    text = resolvedLeftDisplayText,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            icon != null -> {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }

                        // ---- Text field + placeholder ----
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (value.isEmpty() && hintText != null) {
                                Text(
                                    text = hintText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.extendedColors.textDisabled,
                                )
                            }
                            innerTextField()
                        }

                        // ---- Trailing slot ----
                        rightDisplayText?.let {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = it,
                                style = if (isRightTextGreen)
                                    MaterialTheme.typography.titleMedium
                                else
                                    MaterialTheme.typography.bodyMedium,
                                color = if (isRightTextGreen)
                                    MaterialTheme.extendedColors.success
                                else
                                    Color.Unspecified,
                            )
                        }

                        when {
                            suffixIcon != null -> {
                                Spacer(modifier = Modifier.width(8.dp))
                                suffixIcon()
                            }
                            isDropdown -> {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.extendedColors.textDisabled,
                                    modifier = Modifier.size(25.dp),
                                )
                            }
                            obscureText -> {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = if (isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = MaterialTheme.extendedColors.textDisabled,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() },
                                        ) { isHidden = !isHidden },
                                )
                            }
                        }

                        if (maxLength != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${value.length}/$maxLength",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.extendedColors.textSecondary,
                            )
                        }
                    }
                }
            )

            if (isDropdown && onClick != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onClick() }
                )
            }
        }

        if (showError && errorText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
        }
    }
}