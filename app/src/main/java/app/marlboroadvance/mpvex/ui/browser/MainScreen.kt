package app.marlboroadvance.mpvex.ui.browser

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.marlboroadvance.mpvex.presentation.Screen
import app.marlboroadvance.mpvex.ui.browser.folderlist.FolderListScreen
import app.marlboroadvance.mpvex.ui.browser.playlist.PlaylistScreen
import app.marlboroadvance.mpvex.ui.browser.recentlyplayed.RecentlyPlayedScreen
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import app.marlboroadvance.mpvex.ui.theme.VelocityTheme
import app.marlboroadvance.mpvex.ui.theme.velocityAmbientBackground
import app.marlboroadvance.mpvex.ui.theme.LiquidGlassMode
import app.marlboroadvance.mpvex.ui.theme.liquidGlass
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

@Serializable
object MainScreen : Screen {
  // Use a companion object to store state more persistently
  private var persistentSelectedTab: Int = 0

  // Reactive shared state that can be updated by FileSystemBrowserScreen / selection handlers
  private val _isInSelectionMode = MutableStateFlow(false)
  val isInSelectionMode: StateFlow<Boolean> = _isInSelectionMode.asStateFlow()

  private val _shouldHideNavigationBar = MutableStateFlow(false)
  val shouldHideNavigationBar: StateFlow<Boolean> = _shouldHideNavigationBar.asStateFlow()

  private val _sharedVideoSelectionManager = MutableStateFlow<Any?>(null)
  val sharedVideoSelectionManager: StateFlow<Any?> = _sharedVideoSelectionManager.asStateFlow()

  private val _isPermissionDenied = MutableStateFlow(false)

  /**
   * Update selection state and navigation bar visibility
   * This method should be called whenever selection changes
   */
  fun updateSelectionState(
    isInSelectionMode: Boolean,
    isOnlyVideosSelected: Boolean,
    selectionManager: Any?
  ) {
    _isInSelectionMode.value = isInSelectionMode
    _sharedVideoSelectionManager.value = selectionManager
    // Only hide navigation bar when videos are selected AND in selection mode
    _shouldHideNavigationBar.value = isInSelectionMode && isOnlyVideosSelected
  }

  /**
   * Update permission state to control FAB visibility
   */
  fun updatePermissionState(isDenied: Boolean) {
    _isPermissionDenied.value = isDenied
  }

  /**
   * Get current permission denied state
   */
  fun getPermissionDeniedState(): Boolean = _isPermissionDenied.value

  /**
   * Update bottom navigation bar visibility based on floating bottom bar state
   */
  fun updateBottomBarVisibility(shouldShow: Boolean) {
    _shouldHideNavigationBar.value = !shouldShow
  }

  @Composable
  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  override fun Content() {
    var selectedTab by remember {
      mutableIntStateOf(persistentSelectedTab.coerceIn(0, 2))
    }

    val context = LocalContext.current
    val density = LocalDensity.current

    val hideNavigationBar by _shouldHideNavigationBar.collectAsState()

    // Update persistent state whenever tab changes
    LaunchedEffect(selectedTab) {
      persistentSelectedTab = selectedTab
    }

    // Scaffold with bottom navigation bar
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .velocityAmbientBackground(),
      containerColor = Color.Transparent,
      bottomBar = {
        // Animated bottom navigation bar with slide animations
        AnimatedVisibility(
          visible = !hideNavigationBar,
          enter = slideInVertically(
            animationSpec = tween(durationMillis = 250),
            initialOffsetY = { fullHeight -> fullHeight }
          ),
          exit = slideOutVertically(
            animationSpec = tween(durationMillis = 250),
            targetOffsetY = { fullHeight -> fullHeight }
          )
        ) {
          VelocityBottomNavDock(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.navigationBarsPadding()
          )
        }
      }
    ) { paddingValues ->
      Box(modifier = Modifier.fillMaxSize()) {
        // Always use 80dp bottom padding regardless of navigation bar visibility
        val fabBottomPadding = 80.dp

        AnimatedContent(
          targetState = selectedTab,
          transitionSpec = {
            // Material 3 Expressive slide-in-fade animation (snappy 120Hz response)
            val slideDistance = with(density) { 48.dp.roundToPx() }
            val animationDuration = 180
            
            if (targetState > initialState) {
              // Moving forward: slide in from right with fade
              (slideInHorizontally(
                animationSpec = tween(
                  durationMillis = animationDuration,
                  easing = FastOutSlowInEasing
                ),
                initialOffsetX = { slideDistance }
              ) + fadeIn(
                animationSpec = tween(
                  durationMillis = animationDuration,
                  easing = FastOutSlowInEasing
                )
              )) togetherWith (slideOutHorizontally(
                animationSpec = tween(
                  durationMillis = animationDuration,
                  easing = FastOutSlowInEasing
                ),
                targetOffsetX = { -slideDistance }
              ) + fadeOut(
                animationSpec = tween(
                  durationMillis = animationDuration / 2,
                  easing = FastOutSlowInEasing
                )
              ))
            } else {
              // Moving backward: slide in from left with fade
              (slideInHorizontally(
                animationSpec = tween(
                  durationMillis = animationDuration,
                  easing = FastOutSlowInEasing
                ),
                initialOffsetX = { -slideDistance }
              ) + fadeIn(
                animationSpec = tween(
                  durationMillis = animationDuration,
                  easing = FastOutSlowInEasing
                )
              )) togetherWith (slideOutHorizontally(
                animationSpec = tween(
                  durationMillis = animationDuration,
                  easing = FastOutSlowInEasing
                ),
                targetOffsetX = { slideDistance }
              ) + fadeOut(
                animationSpec = tween(
                  durationMillis = animationDuration / 2,
                  easing = FastOutSlowInEasing
                )
              ))
            }
          },
          label = "tab_animation"
        ) { targetTab ->
          CompositionLocalProvider(
            LocalNavigationBarHeight provides fabBottomPadding
          ) {
            when (targetTab) {
              0 -> FolderListScreen.Content()
              1 -> RecentlyPlayedScreen.Content()
              2 -> PlaylistScreen.Content()
              else -> FolderListScreen.Content()
            }
          }
        }
      }
    }
  }
}

// CompositionLocal for navigation bar height
val LocalNavigationBarHeight = compositionLocalOf { 0.dp }

@Composable
fun VelocityBottomNavDock(
  selectedTab: Int,
  onTabSelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val tabs = listOf(
    Triple(Icons.Filled.Home, "Home", "Home"),
    Triple(Icons.Filled.History, "Recents", "Recents"),
    Triple(Icons.AutoMirrored.Filled.PlaylistPlay, "Playlists", "Playlists"),
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    contentAlignment = Alignment.Center,
  ) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .liquidGlass(
          shape = RoundedCornerShape(32.dp),
          mode = LiquidGlassMode.Surface,
          borderWidth = 1.25.dp,
        )
        .clip(RoundedCornerShape(32.dp))
        .padding(horizontal = 6.dp, vertical = 6.dp),
    ) {
      val totalWidth = maxWidth
      val tabWidth = totalWidth / tabs.size
      val pillWidth = 64.dp
      val pillHeight = 32.dp

      val animatedTabIndex by animateFloatAsState(
        targetValue = selectedTab.toFloat(),
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioNoBouncy,
          stiffness = Spring.StiffnessMediumLow,
        ),
        label = "navPillSlide",
      )

      val pillOffsetX = (tabWidth * animatedTabIndex) + (tabWidth - pillWidth) / 2

      // Sliding fluid liquid-glass pill indicator with active specular glow
      Box(
        modifier = Modifier
          .offset(x = pillOffsetX, y = 2.dp)
          .size(width = pillWidth, height = pillHeight)
          .liquidGlass(
            shape = CircleShape,
            mode = LiquidGlassMode.Accent,
            borderWidth = 1.15.dp,
            hasActiveGlow = true,
          )
          .clip(CircleShape),
      )

      // Tab Buttons Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        tabs.forEachIndexed { index, (icon, label, contentDesc) ->
          val isSelected = selectedTab == index
          val interactionSource = remember { MutableInteractionSource() }
          val isHovered by interactionSource.collectIsHoveredAsState()

          val iconScale by animateFloatAsState(
            targetValue = when {
              isSelected -> 1.08f
              isHovered -> 1.05f
              else -> 1f
            },
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioMediumBouncy,
              stiffness = Spring.StiffnessMediumLow,
            ),
            label = "tabIconScale$index",
          )

          Column(
            modifier = Modifier
              .weight(1f)
              .hoverable(interactionSource)
              .pointerHoverIcon(PointerIcon.Hand)
              .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 32.dp, color = Color.White),
                onClick = { onTabSelected(index) },
              )
              .padding(vertical = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Box(
              modifier = Modifier.size(width = 64.dp, height = 32.dp),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                tint = if (isSelected) VelocityTheme.NavActiveIcon else if (isHovered) Color.White else VelocityTheme.NavInactive,
                modifier = Modifier
                  .size(20.dp)
                  .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                  },
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = label,
              fontSize = 11.5.sp,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
              color = if (isSelected) VelocityTheme.NavActiveLabel else if (isHovered) Color.White else VelocityTheme.NavInactive,
              letterSpacing = 0.2.sp,
            )
          }
        }
      }
    }
  }
}