package cat.copernic.easytraza_mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EasyTrazaLightColorScheme = lightColorScheme(
    primary = EasyTrazaPrimary,
    onPrimary = EasyTrazaOnPrimary,
    primaryContainer = EasyTrazaPrimaryContainer,
    onPrimaryContainer = EasyTrazaOnPrimaryContainer,
    secondary = EasyTrazaSecondary,
    onSecondary = EasyTrazaOnSecondary,
    secondaryContainer = EasyTrazaSecondaryContainer,
    onSecondaryContainer = EasyTrazaOnSecondaryContainer,
    tertiary = EasyTrazaTertiary,
    onTertiary = EasyTrazaOnTertiary,
    error = EasyTrazaError,
    onError = EasyTrazaOnError,
    background = EasyTrazaBackground,
    onBackground = EasyTrazaOnBackground,
    surface = EasyTrazaSurface,
    onSurface = EasyTrazaOnSurface,
    outline = EasyTrazaOutline
)

@Composable
fun Projecte4_EasyTraza_EricTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EasyTrazaLightColorScheme,
        typography = Typography,
        content = content
    )
}