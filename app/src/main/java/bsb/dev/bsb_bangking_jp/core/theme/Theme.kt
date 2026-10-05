package bsb.dev.bsb_bangking_jp.core.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val LightColorScheme = lightColorScheme(
    // Primary → aksi utama, tombol, dsb. Sering dipakai sbg bg komponen dgn teks putih
    primary = Primary2,
    onPrimary = White,
    primaryContainer = Primary8,      // bg biru sangat muda
    onPrimaryContainer = Gray950,     // font default di atas container muda

    // Secondary → elemen pendukung (strip intro, bg biru muda, dll)
    secondary = Primary6,
    onSecondary = Primary2,
    secondaryContainer = Secondary100,
    onSecondaryContainer = Primary1,

    // Tertiary → aksen (mis. badge oranye)
    tertiary = Orange500,
    onTertiary = Orange200,
    tertiaryContainer = Orange50,
    onTertiaryContainer = Orange900,

    // Background & Surface
    background = White,
    onBackground = Gray950,
    surface = White,
    onSurface = Gray500,
    surfaceVariant = Gray50,
    onSurfaceVariant = Gray300,

    // Outline & garis
    outline = Gray400,//textfeild
    outlineVariant = Primary100,//testfield border aktif

    // Error
    error = Red500,
    onError = White,
    errorContainer = Red100,
    onErrorContainer = Red900,

    // Inverse (dipakai snackbar, tooltip, dll)
    inverseSurface = Primary7,
    inverseOnSurface = Primary3,
    inversePrimary = Primary5,
    scrim = Black,
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary2,
    onPrimary = Gray950,
    primaryContainer = Primary5,
    onPrimaryContainer = White,

    secondary = Secondary100,
    onSecondary = Black,
    secondaryContainer = Primary1,
    onSecondaryContainer = White,

    tertiary = Orange500,
    onTertiary = White,
    tertiaryContainer = Orange900,
    onTertiaryContainer = Orange100,

    background = Gray950,
    onBackground = White,
    surface = Gray900,
    onSurface = White,
    surfaceVariant = Gray800,
    onSurfaceVariant = Gray500,

    outline = Gray400,
    outlineVariant = Gray800,

    error = Red500,
    onError = White,
    errorContainer = Red900,
    onErrorContainer = Red100,

    inverseSurface = Primary3,
    inverseOnSurface = Primary7,
    inversePrimary = Primary1,

    scrim =  White,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BSBBangkingJPTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    val density = LocalDensity.current
    val cappedDensity = Density(
        density = density.density,
        fontScale = density.fontScale.coerceAtMost(MAX_FONT_SCALE),
    )
    CompositionLocalProvider(
        LocalDensity provides cappedDensity,
        LocalExtendedColors provides extendedColors,
        LocalRippleConfiguration provides null
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
private const val MAX_FONT_SCALE = 1.2f
// Extension biar akses gampang: MaterialTheme.extendedColors.textPrimary
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current