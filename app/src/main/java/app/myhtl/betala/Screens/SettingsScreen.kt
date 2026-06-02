package app.myhtl.betala.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SettingsScreen(navController: NavController){

    var sudokus = listOf("Setting1" ,"Setting2", "Setting3", "Setting4", "Setting5") // Später richtig implementieren

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Header(modifier = Modifier.padding(top = 25.dp), text = "Settings")

            LazyColumn(
                modifier = Modifier.padding(top = 20.dp).weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(15.dp)
            ){
                items(sudokus) { sudokuName ->
                    Text(
                        text = sudokuName,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 18.sp
                    )
                }
            }

            Button(onClick = {
                navController.popBackStack()
            }) {
                Text("Back")
            }
        }


    }
}