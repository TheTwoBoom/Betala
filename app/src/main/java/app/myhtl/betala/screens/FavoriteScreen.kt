package app.myhtl.betala.screens

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
fun FavoriteScreen(navController: NavController){

    var sudokus = listOf("Sudoku1" ,"Sudoku2", "Sudoku3", "Sudoku4", "Sudoku5") // Später richtig implementieren

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Header(modifier = Modifier.padding(top = 25.dp), text = "Your Favorite Sudokus")

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