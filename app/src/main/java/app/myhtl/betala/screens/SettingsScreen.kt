package app.myhtl.betala.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myhtl.betala.R


@Composable
fun SettingsScreen(navController: NavController){

    val settings = listOf("Setting1" ,"Setting2", "Setting3", "Setting4", "Setting5") // Später richtig implementieren
    val context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text(modifier = Modifier.padding(top = 25.dp), text = stringResource(R.string.settings))

            LazyColumn(
                modifier = Modifier.padding(top = 20.dp).weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(15.dp)
            ){
                items(settings) { settingName ->
                    Text(
                        text = settingName,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 18.sp
                    )
                }
            }
            Donate(context)
        }


    }
}