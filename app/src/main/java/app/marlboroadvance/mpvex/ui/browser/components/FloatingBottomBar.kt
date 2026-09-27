package app.marlboroadvance.mpvex.ui.browser.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.marlboroadvance.mpvex.ui.theme.LiquidGlassIconButton
import app.marlboroadvance.mpvex.ui.theme.LiquidGlassMode
import app.marlboroadvance.mpvex.ui.theme.liquidGlass

/**
 * Material 3 Floating Button Bar for file/folder operations
 * Translucent liquid glass capsule with spring micro-interaction buttons
 */
@Composable
fun BrowserBottomBar(
  isSelectionMode: Boolean,
  onCopyClick: () -> Unit,
  onMoveClick: () -> Unit,
  onRenameClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onAddToPlaylistClick: () -> Unit,
  modifier: Modifier = Modifier,
  showCopy: Boolean = true,
  showMove: Boolean = true,
  showRename: Boolean = true,
  showDelete: Boolean = true,
  showAddToPlaylist: Boolean = true,
) {
  AnimatedVisibility(
    visible = isSelectionMode,
    modifier = modifier,
    enter = fadeIn(),
    exit = fadeOut(),
  ) {
    Box(
      modifier = Modifier
        .windowInsetsPadding(WindowInsets.systemBars)
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .liquidGlass(
          shape = RoundedCornerShape(32.dp),
          mode = LiquidGlassMode.Surface,
          borderWidth = 1.25.dp,
        )
        .clip(RoundedCornerShape(32.dp)),
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        LiquidGlassIconButton(
          onClick = onCopyClick,
          enabled = showCopy,
          size = 46.dp,
          iconSize = 22.dp,
          mode = LiquidGlassMode.Regular,
          contentDescription = "Copy",
          icon = {
            Icon(
              Icons.Filled.ContentCopy,
              contentDescription = "Copy",
              modifier = Modifier.size(22.dp),
            )
          },
        )

        LiquidGlassIconButton(
          onClick = onMoveClick,
          enabled = showMove,
          size = 46.dp,
          iconSize = 22.dp,
          mode = LiquidGlassMode.Regular,
          contentDescription = "Move",
          icon = {
            Icon(
              Icons.AutoMirrored.Filled.DriveFileMove,
              contentDescription = "Move",
              modifier = Modifier.size(22.dp),
            )
          },
        )

        LiquidGlassIconButton(
          onClick = onRenameClick,
          enabled = showRename,
          size = 46.dp,
          iconSize = 22.dp,
          mode = LiquidGlassMode.Regular,
          contentDescription = "Rename",
          icon = {
            Icon(
              Icons.Filled.DriveFileRenameOutline,
              contentDescription = "Rename",
              modifier = Modifier.size(22.dp),
            )
          },
        )

        LiquidGlassIconButton(
          onClick = onAddToPlaylistClick,
          enabled = showAddToPlaylist,
          size = 46.dp,
          iconSize = 22.dp,
          mode = LiquidGlassMode.Regular,
          contentDescription = "Add to Playlist",
          icon = {
            Icon(
              Icons.AutoMirrored.Filled.PlaylistAdd,
              contentDescription = "Add to Playlist",
              modifier = Modifier.size(22.dp),
            )
          },
        )

        LiquidGlassIconButton(
          onClick = onDeleteClick,
          enabled = showDelete,
          size = 46.dp,
          iconSize = 22.dp,
          mode = LiquidGlassMode.Error,
          contentDescription = "Delete",
          icon = {
            Icon(
              Icons.Filled.Delete,
              contentDescription = "Delete",
              modifier = Modifier.size(22.dp),
            )
          },
        )
      }
    }
  }
}
