package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.util.InitialName
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun InitialAvatar(
    initials: String,
    imagePath: String? = null,
    photoBytes: ByteArray? = null,
    radius: Double = 28.0,
    isSelected: Boolean = false,
    showBorder: Boolean = false,
) {
    val colorScheme = MaterialTheme.colorScheme

    val backgroundColor = if (isSelected) colorScheme.onTertiary else colorScheme.primaryContainer
    val textColor = if (isSelected) colorScheme.tertiary else colorScheme.primary
    val borderColor = if (isSelected) colorScheme.tertiary else colorScheme.outlineVariant

    val hasImage = !imagePath.isNullOrEmpty()
    val imageModel: Any? = when {
        photoBytes != null -> photoBytes
        hasImage -> imagePath
        else -> null
    }

    val diameter = (radius * 2).dp
    val borderModifier = if (showBorder) {
        Modifier.border(width = 1.8.dp, color = borderColor, shape = CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .size(diameter)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(borderModifier),
        contentAlignment = Alignment.Center,
    ) {
        // Lapisan dasar: inisial. Ini SENGAJA selalu digambar (bukan di dalam
        // else-branch) supaya otomatis jadi fallback kalau GlideImage di atasnya
        // masih loading atau gagal (mis. tidak ada internet) -- foto yang
        // berhasil load akan menutupinya sepenuhnya begitu selesai.
        Text(
            text = initials.InitialName(),
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = TextUnit((radius * 0.8).toFloat(), TextUnitType.Sp),
            textAlign = TextAlign.Center,
        )

        if (imageModel != null) {
            GlideImage(
                model = imageModel,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(diameter)
                    .clip(CircleShape),
            )
        }
    }
}
