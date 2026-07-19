package app.myhtl.betala

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import app.myhtl.betala.opensudoku.GalleryManager
import app.myhtl.betala.screens.CurrentDevice
import app.myhtl.betala.ui.theme.BetalaTheme
import app.myhtl.betala.screens.GalleryScreen
import app.myhtl.betala.screens.HomeScreen
import app.myhtl.betala.screens.SettingsScreen
import app.myhtl.betala.screens.SudokuScreen
import app.myhtl.betala.screens.WinScreen
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val view = LocalView.current
            val window = (view.context as? Activity)?.window

            if(window != null){
                val insets = WindowCompat.getInsetsController(window, view)
                insets.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                if(CurrentDevice.windowSizeClass() == CurrentDevice.MOBILE_PORTRAIT){
                    insets.show(WindowInsetsCompat.Type.statusBars())

                } else{
                    insets.hide(WindowInsetsCompat.Type.statusBars())
                }
            }


            BetalaTheme {
                BetalaApp()
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            MobileAds.initialize(
                this@MainActivity,
                InitializationConfig.Builder(
                    "ca-app-pub-3940256099942544~3347511713"
                ).build()
            ) {}
        }

        lifecycleScope.launch {
            if (GalleryManager.allSudokus.isEmpty()) {
                GalleryManager.fetchAllSudokus(this@MainActivity)
                GalleryManager.generatePreviews(this@MainActivity)
            }
        }
    }
}
@PreviewScreenSizes
@Composable
fun BetalaApp() {
    val activity: Activity? = LocalActivity.current
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val configuration = LocalConfiguration.current

    val windowSizeClass = adaptiveInfo.windowSizeClass
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destinations ->
                item(
                    icon = {
                        Icon(
                            painterResource(destinations.icon),
                            contentDescription = stringResource(destinations.labelRes),
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    label = {
                        Text(text = stringResource(destinations.labelRes))
                    },
                    selected = currentDestination?.hierarchy?.any { it.route == destinations.route } == true,
                    onClick = {
                        navController.navigate(destinations.route) {
                            //Zurück-Button verweist immer auf StartDestination, also Home
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        layoutType = if (windowSizeClass.minWidthDp >= 840 || isLandscape) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteType.ShortNavigationBarCompact
        }
    ) {
        val sudokuViewModel: SudokuViewModel = viewModel()

        NavHost(
            navController = navController,
            startDestination = AppDestinations.HOME.route
        ) {
            composable(AppDestinations.HOME.route) {
                HomeScreen(navController, sudokuViewModel)
            }
            composable(AppDestinations.SETTINGS.route) {
                SettingsScreen(navController)
            }
            composable(AppDestinations.STORE.route) {
                GalleryScreen(navController, sudokuViewModel)
            }
            composable(AppAdditionalDestinations.SUDOKU.route) {
                SudokuScreen(navController, sudokuViewModel)
            }
            composable(AppAdditionalDestinations.GALLERY.route) {
                GalleryScreen(navController, sudokuViewModel)
            }
            composable(AppAdditionalDestinations.WINSCREEN.route) {
                WinScreen(navController)
            }
        }
    }
}

enum class AppDestinations(
    val labelRes: Int,
    val icon: Int,
    val route: String
) {
    HOME(R.string.home, R.drawable.home, route = "home"),
    STORE(R.string.store, R.drawable.storefront, route = "store"),
    SETTINGS(R.string.settings, R.drawable.settings, route = "settings"),
}

enum class AppAdditionalDestinations(
    val label: String,
    val route: String
) {
    WINSCREEN("Win_Screen", route = "win_screen"),
    SUDOKU("Sudoku_Screen", route = "sudoku_screen"),
    GALLERY("Gallery_Screen", route = "gallery_screen"),
}