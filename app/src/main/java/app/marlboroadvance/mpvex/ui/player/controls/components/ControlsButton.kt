package app.marlboroadvance.mpvex.ui.player.controls.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.marlboroadvance.mpvex.preferences.AppearancePreferences
import app.marlboroadvance.mpvex.preferences.preference.collectAsState
import app.marlboroadvance.mpvex.ui.player.controls.LocalPlayerButtonsClickEvent
import app.marlboroadvance.mpvex.ui.theme.LiquidGlassIconButton
import app.marlboroadvance.mpvex.ui.theme.LiquidGlassMode
import app.marlboroadvance.mpvex.ui.theme.spacing
import org.koin.compose.koinInject

@Composable
fun ControlsButton(
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  onLongClick: () -> Unit = {},
  title: String? = null,
  color: Color? = null,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val appearancePreferences = koinInject<AppearancePreferences>()
  val hideBackground by appearancePreferences.hidePlayerButtonsBackground.collectAsState()
  val clickEvent = LocalPlayerButtonsClickEvent.current

  val glassMode = if (hideBackground) LiquidGlassMode.Clear else LiquidGlassMode.Regular

  LiquidGlassIconButton(
    onClick = {
      clickEvent()
      onClick()
    },
    onLongClick = onLongClick,
    icon = {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = color ?: Color.White,
        modifier = Modifier.size(20.dp),
      )
    },
    contentDescription = title,
    size = 40.dp,
    iconSize = 20.dp,
    mode = glassMode,
    contentColor = color ?: Color.White,
    interactionSource = interactionSource,
    modifier = modifier,
  )
}

@Composable
fun ControlsGroup(
  modifier: Modifier = Modifier,
  content: @Composable RowScope.() -> Unit,
) {
  val spacing = MaterialTheme.spacing

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement =
      androidx.compose.foundation.layout.Arrangement
        .spacedBy(spacing.extraSmall),
    content = content,
  )
}

@Preview
@Composable
private fun PreviewControlsButton() {
  ControlsButton(
    Icons.Default.CatchingPokemon,
    onClick = {},
  )
}
