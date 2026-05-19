package app.myhtl.betala

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
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
    var activity = LocalContext.current as? Activity
    val navController = rememberNavController()

    val currentBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        currentBackStackEntry
            ?.destination
            ?.route

    NavigationSuiteScaffold(

        navigationSuiteItems = {

            AppDestinations.entries.forEach {

                item(

                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label,
                            modifier = Modifier.size(40.dp)
                        )
                    },

                    label = {
                        Text(it.label)
                    },

                    selected = currentRoute == it.route,

                    onClick = {
                        navController.navigate(it.route)
                    }
                )
            }
        }

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
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Header(modifier = Modifier.padding(innerPadding))

                Greeting(
                    modifier = Modifier.padding(innerPadding)
                )

                Row(Modifier
                    .padding(horizontal = 50.dp)
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = {
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Daily challenge")
                    }
                    Button(
                        onClick = {
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Select level")
                    }
                }
                Button(
                    modifier = Modifier.padding(horizontal = 50.dp, vertical = 10.dp),
                    onClick = {
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(text = "Random level")
                }
                Column(Modifier
                    .padding(horizontal = 50.dp, vertical = 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "Enjoying the app? Consider donating to support development!",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                    Button(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                        onClick = {
                            activity?.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "https://buymeacoffee.com/".toUri()
                                ), null)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text(
                            text = "Donate",
                            fontSize = 25.sp
                        )
                    }

                }
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
fun Header(modifier: Modifier = Modifier){
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