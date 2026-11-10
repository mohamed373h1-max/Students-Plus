package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LuxuryDarkColorScheme = darkColorScheme(
  primary = ElectricSapphire,
  onPrimary = Color.White,
  primaryContainer = DeepIndigoBlue,
  onPrimaryContainer = NeonSkyBlue,
  secondary = NeonSkyBlue,
  onSecondary = ObsidianBlack,
  secondaryContainer = SurfaceDarkElevated,
  onSecondaryContainer = GlowBlue,
  tertiary = LuxuryGold,
  onTertiary = ObsidianBlack,
  tertiaryContainer = Color(0xFF452200),
  onTertiaryContainer = LuxuryGoldGlow,
  background = ObsidianBlack,
  onBackground = TextPrimary,
  surface = MidnightBlack,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceDark,
  onSurfaceVariant = TextSecondary,
  outline = SurfaceBorder,
  outlineVariant = SurfaceBorderLight,
  error = RoseAlert,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  // Luxury Blue and Black aesthetic is intentional and signature
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = LuxuryDarkColorScheme,
    typography = Typography,
    content = content
  )
}
