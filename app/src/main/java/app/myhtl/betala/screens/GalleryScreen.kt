package app.myhtl.betala.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import app.myhtl.betala.R
import app.myhtl.betala.opensudoku.GalleryManager
import app.myhtl.betala.opensudoku.GameManager


enum class Destination(
    val route: String,
    val label: String,
    val sudokus: Array<GameManager.OpenSudoku>
) {
    ALL("all", "All", GalleryManager.getAllSudokus()),
    FAVORITES("favorites", "Favorites", GalleryManager.getFavoriteSudokus()),
}
@Composable
fun GalleryScreen(navController: NavController, startDestination: Destination){
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Header(modifier = Modifier.padding(top = 25.dp), text = stringResource(R.string.gallery_header))
            Scaffold { contentPadding ->
                PrimaryTabRow(selectedTabIndex = selectedDestination, modifier = Modifier.padding(contentPadding)) {
                    Destination.entries.forEachIndexed { index, destination ->
                        Tab(
                            selected = selectedDestination == index,
                            onClick = {
                                navController.navigate(route = destination.route)
                                selectedDestination = index
                            },
                            text = {
                                Text(
                                    text = destination.label,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }
            }
            LazyColumn(
                modifier = Modifier.padding(top = 20.dp).weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(15.dp)
            ){
                items(5) { sudokuName ->
                    Text(
                        text = "Text",
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