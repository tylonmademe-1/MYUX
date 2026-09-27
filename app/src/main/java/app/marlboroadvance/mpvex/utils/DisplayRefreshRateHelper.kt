package app.marlboroadvance.mpvex.utils

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.Display
import android.view.WindowManager

/**
 * Helper to force and manage real 120 Hz display refresh rate across the application.
 * Configures display mode and window parameters to eliminate lag
 * and provide ultra-smooth 120 FPS rendering, scrolling, and instantaneous touch responses.
 */
object DisplayRefreshRateHelper {
  private const val TAG = "DisplayRefreshRate"

  fun applyRefreshRate(activity: Activity, force120Hz: Boolean) {
    try {
      val window = activity.window ?: return
      val params = window.attributes ?: return

      if (force120Hz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
              activity.display
            } catch (_: Throwable) {
              @Suppress("DEPRECATION")
              activity.windowManager.defaultDisplay
            }
          } else {
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay
          }

          if (display != null) {
            val modes = display.supportedModes
            // Find display mode with highest refresh rate, prioritizing >= 119Hz
            val bestMode = modes
              .filter { it.refreshRate >= 119f }
              .maxByOrNull { it.refreshRate }
              ?: modes.filter { it.refreshRate > 60f }.maxByOrNull { it.refreshRate }

            if (bestMode != null) {
              params.preferredDisplayModeId = bestMode.modeId
              params.preferredRefreshRate = bestMode.refreshRate
            } else {
              params.preferredRefreshRate = 120f
            }
          } else {
            params.preferredRefreshRate = 120f
          }
        }

        // Try reflection-based frame rate APIs safely for newer Android versions
        try {
          val minField = params.javaClass.getField("preferredMinDisplayRefreshRate")
          val maxField = params.javaClass.getField("preferredMaxDisplayRefreshRate")
          minField.setFloat(params, 120f)
          maxField.setFloat(params, 120f)
        } catch (_: Throwable) { }

        try {
          val setFrameRateMethod = window.decorView.javaClass.getMethod(
            "setFrameRate",
            Float::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
          )
          setFrameRateMethod.invoke(window.decorView, 120f, 0)
        } catch (_: Throwable) { }
      } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          params.preferredDisplayModeId = 0
          params.preferredRefreshRate = 0f
        }

        try {
          val minField = params.javaClass.getField("preferredMinDisplayRefreshRate")
          val maxField = params.javaClass.getField("preferredMaxDisplayRefreshRate")
          minField.setFloat(params, 0f)
          maxField.setFloat(params, 0f)
        } catch (_: Throwable) { }

        try {
          val setFrameRateMethod = window.decorView.javaClass.getMethod(
            "setFrameRate",
            Float::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
          )
          setFrameRateMethod.invoke(window.decorView, 0f, 0)
        } catch (_: Throwable) { }
      }

      window.attributes = params
    } catch (e: Throwable) {
      Log.w(TAG, "Failed to apply display refresh rate: ${e.message}")
    }
  }
}
