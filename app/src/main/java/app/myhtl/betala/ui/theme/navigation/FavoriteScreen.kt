package app.myhtl.betala.ui.theme.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material3.Text

@Composable
fun FavoriteScreen(navController: NavController){
    Column {
        Text("Settings")

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text("Back")
        }
    }
}