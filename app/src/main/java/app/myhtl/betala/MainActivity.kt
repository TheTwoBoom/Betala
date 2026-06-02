package app.myhtl.betala

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.myhtl.betala.ui.theme.BetalaTheme
import app.myhtl.betala.ui.theme.navigation.FavoriteScreen
import app.myhtl.betala.ui.theme.navigation.HomeScreen
import app.myhtl.betala.ui.theme.navigation.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BetalaTheme {
                BetalaApp()

            }
        }
    }
}

@SuppressLint("ContextCastToActivity")
@PreviewScreenSizes
@Composable
fun BetalaApp() {
    val activity: Activity = LocalContext.current as Activity
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var selectedItemIndex by remember { mutableIntStateOf(0) }
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEachIndexed { index, destinations ->
                item(
                    icon = {
                        Icon(
                            painterResource(destinations.icon),
                            contentDescription = destinations.label,
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    label = {
                        Text(text = destinations.label)
                    },
                    selected = index == selectedItemIndex,
                    onClick = {
                        selectedItemIndex = index
                        navController.navigate(destinations.route)
                    }
                )
            }
        },
        layoutType = NavigationSuiteType.ShortNavigationBarCompact
    ) {



        NavHost(
            navController = navController,
            startDestination = AppDestinations.HOME.route
        ) {
            composable(AppDestinations.HOME.route) {
                HomeScreen(navController)
            }
            composable(AppDestinations.SETTINGS.route) {
                SettingsScreen(navController)
            }
            composable(AppDestinations.FAVORITES.route) {
                FavoriteScreen(navController)
            }
        }

    }

}

enum class AppDestinations(
    val label: String,
    val icon: Int,
    val route: String
) {

    FAVORITES("Favorites", R.drawable.ic_favorite, route = "favorites"),
    HOME("Home", R.drawable.ic_home, route = "home"),
    SETTINGS("Settings", R.drawable.outline_settings_24, route = "settings"),

}


@Composable
fun Header(modifier: Modifier = Modifier) {
    Text(
        text = "Betala",
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 100.sp
    )

}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to Betala - Sudoku Variants!",
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 20.sp,
        modifier = modifier.padding(10.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BetalaTheme {
        Greeting()
    }
}