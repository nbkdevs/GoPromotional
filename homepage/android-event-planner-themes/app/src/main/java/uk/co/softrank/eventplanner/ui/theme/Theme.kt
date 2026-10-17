package uk.co.softrank.eventplanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = lightColorScheme(
    primary = Forest,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = ForestLight.copy(alpha = 0.18f),
    onPrimaryContainer = ForestDark,
    secondary = Clay,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Clay.copy(alpha = 0.14f),
    onSecondaryContainer = Clay,
    background = Mist,
    onBackground = Ink,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = Ink,
    surfaceVariant = Mist,
    onSurfaceVariant = Slate,
    outline = Line,
    error = Danger,
    onError = androidx.compose.ui.graphics.Color.White
)

@Composable
fun EventPlannerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}
