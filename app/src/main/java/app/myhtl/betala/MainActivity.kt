package app.myhtl.betala

import android.annotation.SuppressLint
import android.content.res.Configuration

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import app.myhtl.betala.opensudoku.GameManager
import app.myhtl.betala.ui.theme.BetalaTheme
import app.myhtl.betala.screens.FavoriteScreen
import app.myhtl.betala.screens.HomeScreen
import app.myhtl.betala.screens.SettingsScreen
import app.myhtl.betala.screens.SudokuScreen
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BetalaTheme {
                BetalaApp()
            }
        }
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize GMA Next-Gen SDK on a background thread.
            MobileAds.initialize(
                this@MainActivity,
                InitializationConfig.Builder("ca-app-pub-3940256099942544~3347511713").build()
            ) {
                // Adapter initialization is complete.
            }
            // SDK initialization is complete. If you don't want to wait for bidding adapters to finish
            // initializing, start loading ads now.
            println(MobileAds.getInitializationStatus())
        }
    }
}

@SuppressLint("ContextCastToActivity")
@PreviewScreenSizes
@Composable
fun BetalaApp() {
    //val activity: Activity = LocalContext.current as Activity
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
            composable(AppDestinations.FAVORITES.route) {
                FavoriteScreen(navController)
            }
            composable(AppAdditionalDestinations.SUDOKU.route) {
                SudokuScreen(navController, sudokuViewModel)            }
        }

    }

}

class SudokuViewModel : ViewModel() {
    var selectedIndex by mutableStateOf(0)
    var currentGame by mutableStateOf<GameManager.SudokuGame?>(null)

    fun setIndex(index: Int){
        selectedIndex = index
    }

    fun onNumberSelected(number: Int){

        if(selectedIndex != null && number != null){
            currentGame?.changeValue(selectedIndex,number)
        }
        validateSudoku()
    }

    fun validateSudoku(){
       // if (!(currentGame?.checkCorrect() ?: true)){

       // }
    }
}

enum class AppDestinations(
    val labelRes: Int,
    val icon: Int,
    val route: String
) {

    FAVORITES(R.string.favorites, R.drawable.ic_favorite, route = "favorites"),
    HOME(R.string.home, R.drawable.ic_home, route = "home"),
    SETTINGS(R.string.settings, R.drawable.outline_settings_24, route = "settings"),
}

enum class AppAdditionalDestinations(
    val label: String,
    val route: String
) {

    SUDOKU("Sudoku_Screen", route = "sudoku_screen"),
}