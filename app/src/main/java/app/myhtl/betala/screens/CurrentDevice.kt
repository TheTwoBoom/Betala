package app.myhtl.betala.screens

import android.content.res.Configuration
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.window.core.layout.WindowSizeClass

enum class CurrentDevice {
    MOBILE_PORTRAIT,
    MOBILE_LANDSCAPE,
    TABLET_PORTRAIT,
    TABLET_LANDSCAPE,
    DESKTOP;

    companion object  {
        @Composable
        fun windowSizeClass(): CurrentDevice {
            val configuration = LocalConfiguration.current.orientation

            val windowSizeClass = currentWindowAdaptiveInfo(supportLargeAndXLargeWidth = true).windowSizeClass
            val width = windowSizeClass.minWidthDp
            return when{
                //in progress
                windowSizeClass.isHeightAtLeastBreakpoint(0) &&
                        windowSizeClass.isWidthAtLeastBreakpoint(600 ) -> CurrentDevice.MOBILE_LANDSCAPE
                windowSizeClass.isHeightAtLeastBreakpoint(480 ) &&
                        windowSizeClass.isWidthAtLeastBreakpoint(0) -> CurrentDevice.MOBILE_PORTRAIT
               else -> DESKTOP
            }
        }

    }
}