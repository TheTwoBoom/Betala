package app.myhtl.betala.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import app.myhtl.betala.AppAdditionalDestinations
import androidx.compose.ui.res.stringResource
import app.myhtl.betala.R
@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen(navController: NavController){
    val activity: Activity = LocalContext.current as Activity
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MainHeader(modifier = Modifier.padding(innerPadding))

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
                    navController.navigate(AppAdditionalDestinations.SUDOKU.route)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text(text = stringResource(R.string.random_level))
            }
            Column(
                Modifier.padding(horizontal = 50.dp, vertical = 25.dp),
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
                        activity.startActivity(
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