package app.marlboroadvance.mpvex.ui.browser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.ViewComfy
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import app.marlboroadvance.mpvex.R
import app.marlboroadvance.mpvex.preferences.AppearancePreferences
import app.marlboroadvance.mpvex.preferences.preference.collectAsState
import app.marlboroadvance.mpvex.ui.theme.VelocityTheme
import app.marlboroadvance.mpvex.ui.theme.DarkMode
import app.marlboroadvance.mpvex.ui.theme.LocalThemeTransitionState
import app.marlboroadvance.mpvex.ui.theme.LiquidGlassIconButton
import app.marlboroadvance.mpvex.ui.theme.LiquidGlassMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Unified top bar for browser screens that switches between normal and selection modes
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BrowserTopBar(
  title: String,
  isInSelectionMode: Boolean,
  selectedCount: Int,
  totalCount: Int,
  onCancelSelection: () -> Unit,
  modifier: Modifier = Modifier,
  onBackClick: (() -> Unit)? = null,
  onSortClick: (() -> Unit)? = null,
  onSearchClick: (() -> Unit)? = null,
  onSettingsClick: (() -> Unit)? = null,
  onDeleteClick: (() -> Unit)? = null,
  onRenameClick: (() -> Unit)? = null,
  isSingleSelection: Boolean = false,
  onInfoClick: (() -> Unit)? = null,
  onShareClick: (() -> Unit)? = null,
  onPlayClick: (() -> Unit)? = null,
  onBlacklistClick: (() -> Unit)? = null,
  onSelectAll: (() -> Unit)? = null,
  onInvertSelection: (() -> Unit)? = null,
  onDeselectAll: (() -> Unit)? = null,
  additionalActions: @Composable RowScope.() -> Unit = { },
  onTitleLongPress: (() -> Unit)? = null,
  useRemoveIcon: Boolean = false,
  onAddToPlaylistClick: (() -> Unit)? = null,
) {
  if (isInSelectionMode) {
    SelectionTopBar(
      selectedCount = selectedCount,
      totalCount = totalCount,
      onCancel = onCancelSelection,
      onDelete = onDeleteClick,
      onRename = onRenameClick,
      isSingleSelection = isSingleSelection,
      onInfo = onInfoClick,
      onShare = onShareClick,
      onPlay = onPlayClick,
      onBlacklist = onBlacklistClick,
      onSelectAll = onSelectAll,
      onInvertSelection = onInvertSelection,
      onDeselectAll = onDeselectAll,
      modifier = modifier,
      useRemoveIcon = useRemoveIcon,
      onAddToPlaylist = onAddToPlaylistClick,
    )
  } else {
    NormalTopBar(
      title = title,
      onBackClick = onBackClick,
      onSortClick = onSortClick,
      onSearchClick = onSearchClick,
      onSettingsClick = onSettingsClick,
      additionalActions = additionalActions,
      modifier = modifier,
      onTitleLongPress = onTitleLongPress,
    )
  }
}

@Composable
fun LiquidHeaderButton(
  onClick: () -> Unit,
  icon: @Composable () -> Unit,
  contentDescription: String,
  modifier: Modifier = Modifier,
) {
  LiquidGlassIconButton(
    onClick = onClick,
    icon = icon,
    contentDescription = contentDescription,
    size = 40.dp,
    iconSize = 20.dp,
    mode = LiquidGlassMode.Regular,
    modifier = modifier,
  )
}

/**
 * Normal mode top bar
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NormalTopBar(
  title: String,
  onBackClick: (() -> Unit)?,
  onSortClick: (() -> Unit)?,
  onSearchClick: (() -> Unit)?,
  onSettingsClick: (() -> Unit)?,
  additionalActions: @Composable RowScope.() -> Unit,
  modifier: Modifier = Modifier,
  onTitleLongPress: (() -> Unit)?,
) {
  val preferences = koinInject<AppearancePreferences>()
  val darkMode by preferences.darkMode.collectAsState()
  val darkTheme = isSystemInDarkTheme()
  val themeTransition = LocalThemeTransitionState.current
  val coroutineScope = rememberCoroutineScope()
  
  // Track title bounds for animation position
  val titleBounds = remember { mutableStateOf(Rect.Zero) }
  
  // Helper function to toggle dark mode
  fun toggleDarkMode() {
    when (darkMode) {
      DarkMode.System -> if (darkTheme) {
        preferences.darkMode.set(DarkMode.Light)
      } else {
        preferences.darkMode.set(DarkMode.Dark)
      }
      DarkMode.Light -> if (darkTheme) {
        preferences.darkMode.set(DarkMode.System)
      } else {
        preferences.darkMode.set(DarkMode.Dark)
      }
      DarkMode.Dark -> if (darkTheme) {
        preferences.darkMode.set(DarkMode.Light)
      } else {
        preferences.darkMode.set(DarkMode.System)
      }
    }
  }

  TopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = Color.Transparent,
    ),
    title = {
      val titleModifier = Modifier
        .onGloballyPositioned { coordinates ->
          titleBounds.value = coordinates.boundsInWindow()
        }
        .pointerInput(onTitleLongPress) {
          detectTapGestures(
            onTap = { localOffset ->
              // Don't allow theme change if animation is in progress
              if (themeTransition?.isAnimating == true) return@detectTapGestures
              
              // Calculate window position for circular reveal
              val windowOffset = Offset(
                titleBounds.value.left + localOffset.x,
                titleBounds.value.top + localOffset.y
              )
              themeTransition?.startTransition(windowOffset)
              // Delay theme change to allow overlay to display first
              coroutineScope.launch {
                toggleDarkMode()
              }
            },
            onLongPress = if (onTitleLongPress != null) {
              { onTitleLongPress() }
            } else null
          )
        }

      if (onBackClick == null) {
        Text(
          title,
          style = TextStyle(
            brush = Brush.verticalGradient(VelocityTheme.TitleGradient),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = titleModifier.padding(start = 6.dp),
        )
      } else {
        Text(
          title,
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
          ),
          color = Color.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = titleModifier,
        )
      }
    },
    navigationIcon = {
      if (onBackClick != null) {
        LiquidHeaderButton(
          onClick = onBackClick,
          icon = {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.back),
              modifier = Modifier.size(20.dp),
            )
          },
          contentDescription = stringResource(R.string.back),
          modifier = Modifier.padding(start = 12.dp, end = 4.dp),
        )
      }
    },
    actions = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(end = 12.dp)
      ) {
        additionalActions()
        if (onSearchClick != null) {
          LiquidHeaderButton(
            onClick = onSearchClick,
            icon = {
              Icon(
                Icons.Filled.Search,
                contentDescription = "Search",
                modifier = Modifier.size(20.dp),
              )
            },
            contentDescription = "Search",
          )
        }
        if (onSortClick != null) {
          LiquidHeaderButton(
            onClick = onSortClick,
            icon = {
              Icon(
                Icons.Filled.GridView,
                contentDescription = stringResource(R.string.sort),
                modifier = Modifier.size(20.dp),
              )
            },
            contentDescription = stringResource(R.string.sort),
          )
        }
        if (onSettingsClick != null) {
          LiquidHeaderButton(
            onClick = onSettingsClick,
            icon = {
              Icon(
                Icons.Filled.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(20.dp),
              )
            },
            contentDescription = "Settings",
          )
        }
      }
    },
    modifier = modifier,
  )
}

/**
 * Selection mode top bar
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SelectionTopBar(
  selectedCount: Int,
  totalCount: Int,
  onCancel: () -> Unit,
  onDelete: (() -> Unit)?,
  onRename: (() -> Unit)?,
  isSingleSelection: Boolean,
  onInfo: (() -> Unit)?,
  onShare: (() -> Unit)?,
  onPlay: (() -> Unit)?,
  onBlacklist: (() -> Unit)?,
  onSelectAll: (() -> Unit)?,
  onInvertSelection: (() -> Unit)?,
  onDeselectAll: (() -> Unit)?,
  modifier: Modifier = Modifier,
  useRemoveIcon: Boolean = false,
  onAddToPlaylist: (() -> Unit)? = null,
) {
  var showDropdown by remember { mutableStateOf(false) }

  TopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = if (MaterialTheme.colorScheme.background == Color.Black) {
        Color.Black
      } else {
        MaterialTheme.colorScheme.surfaceContainer
      },
    ),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { showDropdown = true },
      ) {
        Text(
          stringResource(R.string.selected_items, selectedCount, totalCount),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.primary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Icon(
          Icons.Filled.ArrowDropDown,
          contentDescription = stringResource(R.string.selection_options),
          modifier = Modifier.size(24.dp),
          tint = MaterialTheme.colorScheme.primary,
        )

        DropdownMenu(
          expanded = showDropdown,
          onDismissRequest = { showDropdown = false },
        ) {
          if (onSelectAll != null) {
            DropdownMenuItem(
              text = { Text(stringResource(R.string.select_all)) },
              onClick = {
                onSelectAll()
                showDropdown = false
              },
            )
          }
          if (onInvertSelection != null) {
            DropdownMenuItem(
              text = { Text(stringResource(R.string.invert_selection)) },
              onClick = {
                onInvertSelection()
                showDropdown = false
              },
            )
          }
          if (onDeselectAll != null) {
            DropdownMenuItem(
              text = { Text(stringResource(R.string.deselect_all)) },
              onClick = {
                onDeselectAll()
                showDropdown = false
              },
            )
          }
        }
      }
    },
    navigationIcon = {
      LiquidGlassIconButton(
        onClick = onCancel,
        icon = {
          Icon(
            Icons.Filled.Close,
            contentDescription = stringResource(R.string.generic_cancel),
            modifier = Modifier.size(20.dp),
          )
        },
        contentDescription = stringResource(R.string.generic_cancel),
        size = 38.dp,
        iconSize = 20.dp,
        mode = LiquidGlassMode.Regular,
        modifier = Modifier.padding(start = 8.dp, end = 4.dp),
      )
    },
    actions = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(end = 8.dp),
      ) {
        // Play icon
        if (onPlay != null) {
          LiquidGlassIconButton(
            onClick = onPlay,
            icon = {
              Icon(
                Icons.Filled.PlayArrow,
                contentDescription = "Play",
                modifier = Modifier.size(20.dp),
              )
            },
            contentDescription = "Play",
            size = 38.dp,
            iconSize = 20.dp,
            mode = LiquidGlassMode.Accent,
          )
        }

        // Add to Playlist icon (for Play Store builds)
        if (onAddToPlaylist != null) {
          LiquidGlassIconButton(
            onClick = onAddToPlaylist,
            icon = {
              Icon(
                Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = "Add to Playlist",
                modifier = Modifier.size(20.dp),
              )
            },
            contentDescription = "Add to Playlist",
            size = 38.dp,
            iconSize = 20.dp,
            mode = LiquidGlassMode.Regular,
          )
        }

        // Rename icon
        if (onRename != null) {
          LiquidGlassIconButton(
            onClick = onRename,
            enabled = isSingleSelection,
            icon = {
              Icon(
                Icons.Filled.DriveFileRenameOutline,
                contentDescription = stringResource(R.string.rename),
                modifier = Modifier.size(20.dp),
              )
            },
            contentDescription = stringResource(R.string.rename),
            size = 38.dp,
            iconSize = 20.dp,
            mode = LiquidGlassMode.Regular,
          )
        }

        // Info icon
        if (onInfo != null) {
          LiquidGlassIconButton(
            onClick = onInfo,
            enabled = isSingleSelection,
            icon = {
              Icon(
                Icons.Filled.Info,
                contentDescription = stringResource(R.string.info),
                modifier = Modifier.size(20.dp),
              )
            },
            contentDescription = stringResource(R.string.info),
            size = 38.dp,
            iconSize = 20.dp,
            mode = LiquidGlassMode.Regular,
          )
        }

        // Share icon
        if (onShare != null) {
          LiquidGlassIconButton(
            onClick = onShare,
            icon = {
              Icon(
                Icons.Filled.Share,
                contentDescription = stringResource(R.string.generic_share),
                modifier = Modifier.size(20.dp),
              )
            },
            contentDescription = stringResource(R.string.generic_share),
            size = 38.dp,
            iconSize = 20.dp,
            mode = LiquidGlassMode.Regular,
          )
        }

        // Blacklist icon
        if (onBlacklist != null) {
          LiquidGlassIconButton(
            onClick = onBlacklist,
            icon = {
              Icon(
                Icons.Filled.Block,
                contentDescription = stringResource(R.string.pref_folders_blacklist),
                modifier = Modifier.size(20.dp),
              )
            },
            contentDescription = stringResource(R.string.pref_folders_blacklist),
            size = 38.dp,
            iconSize = 20.dp,
            mode = LiquidGlassMode.Regular,
          )
        }

        // Delete/Remove icon
        if (onDelete != null) {
          LiquidGlassIconButton(
            onClick = onDelete,
            icon = {
              Icon(
                imageVector = if (useRemoveIcon) Icons.Filled.RemoveCircle else Icons.Filled.Delete,
                contentDescription = stringResource(R.string.delete),
                modifier = Modifier.size(20.dp),
              )
            },
            contentDescription = stringResource(R.string.delete),
            size = 38.dp,
            iconSize = 20.dp,
            mode = LiquidGlassMode.Error,
          )
        }
      }
    },
    modifier = modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
  )
}
