package app.marlboroadvance.mpvex.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

/**
 * Design system colors and styles matching the Velocity liquid-glass aesthetic.
 */
object VelocityTheme {
  val PureBlack = Color(0xFF000000)
  val DeepNavyDock = Color(0xF012141A)
  val DockBorder = Color(0x22FFFFFF)
  val PillActiveBorder = Color(0x5AB4D2FF)

  // Gradient text colors
  val TitleGradient = listOf(
    Color(0xFFE1EBFF),
    Color(0xFFB4CEFC),
    Color(0xFF8EB5FA),
  )

  // Card liquid-glass gradients
  val CardGlassGradient = listOf(
    Color(0x15FFFFFF),
    Color(0x06FFFFFF),
    Color(0x0AFFFFFF),
  )
  val CardBorder = Color(0x1FFFFFFF)

  // Folder icon squircle gradient
  val IconBoxGradient = listOf(
    Color(0x35A5BAFF),
    Color(0x18556ECD),
  )
  val IconBoxBorder = Color(0x38B4C8FF)
  val FolderLavender = Color(0xFFC5D2F8)

  // Metadata pills
  val PillBackground = Color(0x12FFFFFF)
  val PillBorder = Color(0x20FFFFFF)
  val PillText = Color(0xFFD7DEEC)

  // FAB electric gradient
  val FabGradient = listOf(
    Color(0xFF3284FF),
    Color(0xFF0D59E3),
    Color(0xFF0940B5),
  )
  val FabBorder = Color(0x52FFFFFF)

  // Navigation dock colors
  val NavActiveIcon = Color(0xFFBDD5FF)
  val NavActiveLabel = Color(0xFFB6D2FF)
  val NavInactive = Color(0xFF8E95A5)
  val NavPillGradient = listOf(
    Color(0x38A5C4FF),
    Color(0x484673DC),
  )
}

/**
 * Tactile micro-interaction spring press & hover effect.
 * Instantly scales down with physics-based spring on press, and gently floats up on hover
 * for 120 FPS responsive touch feedback.
 */
@Composable
fun Modifier.tactilePress(
  scaleDown: Float = 0.97f,
  scaleUpHover: Float = 1.025f,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  enableHover: Boolean = true,
): Modifier {
  val isPressed by interactionSource.collectIsPressedAsState()
  val isHovered = if (enableHover) interactionSource.collectIsHoveredAsState().value else false
  val scale by animateFloatAsState(
    targetValue = when {
      isPressed -> scaleDown
      isHovered -> scaleUpHover
      else -> 1f
    },
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMediumLow
    ),
    label = "tactilePress"
  )
  return this
    .hoverable(interactionSource)
    .pointerHoverIcon(PointerIcon.Hand)
    .graphicsLayer {
      scaleX = scale
      scaleY = scale
    }
}

/**
 * Atmospheric ambient lighting background pool for the Velocity liquid dark mode.
 * Draws subtle blurred deep-blue glows behind content without layout overhead.
 */
fun Modifier.velocityAmbientBackground(): Modifier = this.drawBehind {
  // Base pure AMOLED black
  drawRect(color = Color.Black)

  // Top-left ambient pool (subtle blue glow)
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x281D4ED8),
        Color(0x1038BDF8),
        Color.Transparent
      ),
      center = Offset(size.width * 0.1f, 0f),
      radius = size.width * 0.75f
    ),
    radius = size.width * 0.75f,
    center = Offset(size.width * 0.1f, 0f)
  )

  // Mid-right ambient pool (subtle indigo/violet glow)
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x1F4338CA),
        Color(0x0C6366F1),
        Color.Transparent
      ),
      center = Offset(size.width * 0.95f, size.height * 0.4f),
      radius = size.width * 0.65f
    ),
    radius = size.width * 0.65f,
    center = Offset(size.width * 0.95f, size.height * 0.4f)
  )

  // Bottom-left ambient pool (electric blue accent)
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x261E40AF),
        Color(0x0A2563EB),
        Color.Transparent
      ),
      center = Offset(size.width * 0.05f, size.height * 0.85f),
      radius = size.width * 0.7f
    ),
    radius = size.width * 0.7f,
    center = Offset(size.width * 0.05f, size.height * 0.85f)
  )
}
