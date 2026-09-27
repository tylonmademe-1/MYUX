package app.marlboroadvance.mpvex.ui.browser.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.marlboroadvance.mpvex.domain.media.model.VideoFolder
import app.marlboroadvance.mpvex.preferences.AppearancePreferences
import app.marlboroadvance.mpvex.preferences.BrowserPreferences
import app.marlboroadvance.mpvex.preferences.preference.collectAsState
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.ListItemDefaults.verticalAlignment
import org.koin.compose.koinInject
import kotlin.math.pow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import app.marlboroadvance.mpvex.ui.theme.VelocityTheme
import app.marlboroadvance.mpvex.ui.theme.tactilePress
import app.marlboroadvance.mpvex.ui.theme.LiquidGlassBadge
import app.marlboroadvance.mpvex.ui.theme.LiquidGlassMode

@Composable
fun FolderCard(
  folder: VideoFolder,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isRecentlyPlayed: Boolean = false,
  onLongClick: (() -> Unit)? = null,
  isSelected: Boolean = false,
  onThumbClick: () -> Unit = {},
  showDateModified: Boolean = false,
  customIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
  newVideoCount: Int = 0,
  customChipContent: @Composable (() -> Unit)? = null,
  isGridMode: Boolean = false,
) {
  val appearancePreferences = koinInject<AppearancePreferences>()
  val browserPreferences = koinInject<BrowserPreferences>()
  val unlimitedNameLines by appearancePreferences.unlimitedNameLines.collectAsState()
  val showTotalVideosChip by browserPreferences.showTotalVideosChip.collectAsState()
  val showTotalDurationChip by browserPreferences.showTotalDurationChip.collectAsState()
  val showTotalSizeChip by browserPreferences.showTotalSizeChip.collectAsState()
  val showDateChip by browserPreferences.showDateChip.collectAsState()
  val showFolderPath by browserPreferences.showFolderPath.collectAsState()
  val maxLines = if (unlimitedNameLines) Int.MAX_VALUE else 2

  // Remove the redundant folder name from the path
  val parentPath = folder.path.substringBeforeLast("/", folder.path)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .tactilePress(scaleDown = 0.97f)
      .combinedClickable(
        onClick = onClick,
        onLongClick = onLongClick,
      ),
    shape = if (isGridMode) RoundedCornerShape(20.dp) else RoundedCornerShape(28.dp),
    border = BorderStroke(
      1.dp,
      if (isSelected) MaterialTheme.colorScheme.primary else VelocityTheme.CardBorder
    ),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
  ) {
    if (isGridMode) {
      // GRID LAYOUT - Vertical arrangement
      Column(
        modifier = Modifier
          . fillMaxWidth()
          .background(
            if (isSelected) MaterialTheme.colorScheme.tertiary. copy(alpha = 0.3f) else Color.Transparent,
          )
          .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        val folderGridColumnsPortrait by browserPreferences.folderGridColumnsPortrait.collectAsState()
        val folderGridColumnsLandscape by browserPreferences.folderGridColumnsLandscape.collectAsState()
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
        val folderGridColumns = if (isLandscape) folderGridColumnsLandscape else folderGridColumnsPortrait
        val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
        val horizontalPadding = 32.dp
        val spacing = 8.dp

        val thumbWidthDp = if (folderGridColumns > 1) {
          // (screen - padding - total spacing) / columns
          val totalSpacing = spacing * (folderGridColumns - 1)
          ((screenWidthDp - horizontalPadding - totalSpacing) / folderGridColumns).coerceAtLeast(120.dp)
        } else {
          // single column fallback
          160.dp
        }
        val aspect = 16f / 9f
        val thumbHeightDp = thumbWidthDp / aspect

        val context = LocalContext.current
        
        Box(
          modifier = Modifier
            .width(thumbWidthDp)
            .height(thumbHeightDp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .combinedClickable(
              onClick = onThumbClick,
              onLongClick = onLongClick,
            ),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            customIcon ?: Icons.Filled.Folder,
            contentDescription = "Folder",
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.secondary,
          )

          if (newVideoCount > 0) {
            Box(
              modifier =
                Modifier
                  .align(Alignment.TopEnd)
                  .padding(6.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFFD32F2F))
                  .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
              Text(
                text = newVideoCount.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                ),
                color = Color.White,
              )
            }
          }
          
          if (showTotalDurationChip && folder.totalDuration > 0) {
            LiquidGlassBadge(
              text = formatDuration(folder.totalDuration),
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp),
              mode = LiquidGlassMode.Clear,
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          folder.name,
          style = MaterialTheme.typography.titleSmall,
          color = if (isRecentlyPlayed) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
          maxLines = maxLines,
          overflow = TextOverflow. Ellipsis,
          textAlign = androidx.compose.ui. text.style.TextAlign.Center,
        )

        if (showTotalVideosChip && folder.videoCount > 0) {
          Text(
            if (folder.videoCount == 1) "1 Video" else "${folder.videoCount} Videos",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme. onSurfaceVariant,
          )
        }
      }
    } else {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .then(
            if (isSelected) {
              Modifier.background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
            } else {
              Modifier.background(Brush.linearGradient(VelocityTheme.CardGlassGradient))
            }
          )
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        // Folder Icon Box with Liquid Specular Sheen
        Box(
          modifier = Modifier
            .size(66.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(VelocityTheme.IconBoxGradient))
            .border(1.dp, VelocityTheme.IconBoxBorder, RoundedCornerShape(22.dp))
            .combinedClickable(
              onClick = onThumbClick,
              onLongClick = onLongClick,
            ),
          contentAlignment = Alignment.Center,
        ) {
          // Top subtle diagonal sheen reflection
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .offset(x = (-4).dp, y = (-4).dp)
              .size(28.dp)
              .background(
                Brush.radialGradient(
                  listOf(Color(0x35FFFFFF), Color.Transparent)
                ),
                CircleShape
              )
          )

          Icon(
            customIcon ?: Icons.Filled.Folder,
            contentDescription = "Folder",
            modifier = Modifier.size(40.dp),
            tint = VelocityTheme.FolderLavender,
          )

          // Show new video count badge if folder contains new videos
          if (newVideoCount > 0) {
            Box(
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE53935))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
              Text(
                text = newVideoCount.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                ),
                color = Color.White,
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
          modifier = Modifier.weight(1f),
        ) {
          Text(
            folder.name,
            style = MaterialTheme.typography.titleMedium.copy(
              fontSize = 19.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 0.2.sp,
            ),
            color = if (isRecentlyPlayed) MaterialTheme.colorScheme.tertiary else Color.White,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
          )

          if (showFolderPath && parentPath.isNotEmpty()) {
            Text(
              parentPath,
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Normal,
              ),
              color = Color(0xFF98A1B3),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(6.dp))
          } else {
            Spacer(modifier = Modifier.height(6.dp))
          }

          FlowRow(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
          ) {
            if (customChipContent != null) {
              customChipContent()
            }

            if (showTotalVideosChip && folder.videoCount > 0) {
              Box(
                modifier = Modifier
                  .clip(CircleShape)
                  .background(VelocityTheme.PillBackground)
                  .border(1.dp, VelocityTheme.PillBorder, CircleShape)
                  .padding(horizontal = 14.dp, vertical = 4.dp),
              ) {
                Text(
                  text = if (folder.videoCount == 1) "1 Video" else "${folder.videoCount} Videos",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                  ),
                  color = VelocityTheme.PillText,
                )
              }
            }

            if (showTotalSizeChip && folder.totalSize > 0) {
              Box(
                modifier = Modifier
                  .clip(CircleShape)
                  .background(VelocityTheme.PillBackground)
                  .border(1.dp, VelocityTheme.PillBorder, CircleShape)
                  .padding(horizontal = 14.dp, vertical = 4.dp),
              ) {
                Text(
                  text = formatFileSize(folder.totalSize),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                  ),
                  color = VelocityTheme.PillText,
                )
              }
            }

            if (showTotalDurationChip && folder.totalDuration > 0) {
              Box(
                modifier = Modifier
                  .clip(CircleShape)
                  .background(VelocityTheme.PillBackground)
                  .border(1.dp, VelocityTheme.PillBorder, CircleShape)
                  .padding(horizontal = 14.dp, vertical = 4.dp),
              ) {
                Text(
                  text = formatDuration(folder.totalDuration),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                  ),
                  color = VelocityTheme.PillText,
                )
              }
            }

            if (showDateChip && folder.lastModified > 0) {
              Box(
                modifier = Modifier
                  .clip(CircleShape)
                  .background(VelocityTheme.PillBackground)
                  .border(1.dp, VelocityTheme.PillBorder, CircleShape)
                  .padding(horizontal = 14.dp, vertical = 4.dp),
              ) {
                Text(
                  text = formatDate(folder.lastModified),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                  ),
                  color = VelocityTheme.PillText,
                )
              }
            }
          }
        }
      }
    }
  }
}

private fun formatDuration(durationMs: Long): String {
  val seconds = durationMs / 1000
  val hours = seconds / 3600
  val minutes = (seconds % 3600) / 60
  val secs = seconds % 60

  return when {
    hours > 0 -> "${hours}h ${minutes}m"
    minutes > 0 -> "${minutes}m"
    else -> "${secs}s"
  }
}

private fun formatFileSize(bytes: Long): String {
  if (bytes <= 0) return "0 B"
  val units = arrayOf("B", "KB", "MB", "GB", "TB")
  val digitGroups = (kotlin.math.log10(bytes.toDouble()) / kotlin.math.log10(1024.0)).toInt()
  val value = bytes / 1024.0.pow(digitGroups.toDouble())
  return String.format(java.util.Locale.getDefault(), "%.1f %s", value, units[digitGroups])
}

private fun formatDate(timestampSeconds: Long): String {
  val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
  return sdf.format(java.util.Date(timestampSeconds * 1000))
}
