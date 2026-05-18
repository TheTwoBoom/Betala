package app.myhtl.betala

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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myhtl.betala.ui.theme.BetalaTheme
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

@PreviewScreenSizes
@Composable
fun BetalaApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label ,
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
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
                    Text(text = "Select level")
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
) {

    FAVORITES("Favorites", R.drawable.ic_favorite),
    HOME("Home", R.drawable.ic_home),
    SETTINGS("Settings", R.drawable.outline_settings_24),

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