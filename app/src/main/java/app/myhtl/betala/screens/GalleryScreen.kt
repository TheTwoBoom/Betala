package app.myhtl.betala.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.myhtl.betala.AppAdditionalDestinations
import app.myhtl.betala.R
import app.myhtl.betala.SudokuViewModel
import app.myhtl.betala.opensudoku.GalleryManager
import app.myhtl.betala.opensudoku.GameManager
import app.myhtl.betala.utils.readTextFromUri
import kotlinx.coroutines.runBlocking


enum class Destination(
    val label: String,
    val sudokus: (Context) -> List<GameManager.OpenSudoku>,
) {
    ALL("All", GalleryManager::getAllSudokus),
    FAVORITES("Favorites", GalleryManager::getFavoriteSudokus),
}
@Composable
fun GalleryScreen(navController: NavController, sudokuViewModel: SudokuViewModel, startDestination: Destination){
    val context = LocalContext.current
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
    val sudokuList = remember(selectedDestination, context) {
        Destination.entries[selectedDestination].sudokus(context)
    }
    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { nonNullUri ->
            try {
                GalleryManager.importSudoku(context, nonNullUri)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error parsing file", e)
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Header(modifier = Modifier.padding(top = 25.dp), text = stringResource(R.string.gallery_header))
            PrimaryTabRow(selectedTabIndex = selectedDestination) {
                Destination.entries.forEachIndexed { index, destination ->
                    Tab(
                        selected = selectedDestination == index,
                        onClick = {
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
            LazyColumn(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(15.dp)
            ){
                items(sudokuList) { sudoku ->
                    Button(
                        onClick = {
                            if (sudoku.games.isNotEmpty()) {
                                sudokuViewModel.currentGame = sudoku.games[0]

                                navController.navigate(AppAdditionalDestinations.SUDOKU.route)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text(text = sudoku.name)
                    }
                }
            }
            FloatingActionButton(
                onClick = {
                    getContent.launch(arrayOf("application/xml", "text/xml", "application/opensudoku"))
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 22.dp)
            ) {
                Icon(
                    painterResource(R.drawable.outline_upload_file_24),
                    contentDescription = "Import"
                )
            }
        }
    }
}