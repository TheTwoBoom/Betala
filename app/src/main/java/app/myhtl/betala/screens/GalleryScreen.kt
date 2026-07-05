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
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.myhtl.betala.AppAdditionalDestinations
import app.myhtl.betala.R
import app.myhtl.betala.SudokuViewModel
import app.myhtl.betala.opensudoku.GalleryManager
import app.myhtl.betala.opensudoku.GalleryManager.createBitmapFromSudoku
import app.myhtl.betala.opensudoku.GameManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale


enum class Destination(
    val label: String,
    val sudokus: (Context) -> List<GameManager.OpenSudoku>,
) {
    ALL("All", GalleryManager::getAllSudokus),
    FAVORITES("Favorites", GalleryManager::getFavoriteSudokus),
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GalleryScreen(navController: NavController, sudokuViewModel: SudokuViewModel, startDestination: Destination){
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
    val sudokuList = remember(selectedDestination, context) {
        Destination.entries[selectedDestination].sudokus(context)
    }
    var expanded by remember { mutableStateOf(false) }
    val loading = false
    val filters: SnapshotStateMap<String, String> = SnapshotStateMap()
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { nonNullUri ->
            try {
                GalleryManager.importSudoku(context, nonNullUri)
            } catch (e: Exception) {
                Log.e("GalleryScreen", "Error importing sudoku", e)
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Header(
                modifier = Modifier.padding(top = 25.dp),
                text = stringResource(R.string.gallery_header)
            )

            PrimaryTabRow(
                selectedTabIndex = selectedDestination,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Destination.entries.forEachIndexed { index, destination ->
                    Tab(
                        selected = selectedDestination == index,
                        onClick = { selectedDestination = index },
                        text = {
                            Text(
                                text = destination.label,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
            }
            if (loading) {
                Column(
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { LoadingIndicator() }
            } else {
                Box(modifier = Modifier
                    .wrapContentSize(Alignment.TopStart)) {
                    FilterChip(
                        onClick = { expanded = !expanded },
                        label = {
                            Text(
                                filters.getOrDefault("level", "Difficulty")
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
                        },
                        selected = filters.contains("level"),
                        leadingIcon = {
                            if (filters.contains("level")) {
                                Icon(
                                    painterResource(R.drawable.outline_check),
                                    contentDescription = "Done icon",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        },
                        trailingIcon = {
                            Icon(
                                painterResource(R.drawable.outline_arrow_drop_down),
                                contentDescription = "Open icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        for (filter in listOf("Easy", "Medium", "Hard")) {
                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                    filters["level"] = filter
                                },
                                text = { Text(filter) },
                                selected = filters["level"] == filter,
                                shapes = MenuDefaults.itemShapes()
                            )
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(10.dp),
                ) {
                    items(sudokuList) { sudoku ->
                        OutlinedCard {
                            ListItem(
                                modifier = Modifier.padding(5.dp),
                                onClick = {
                                    if (sudoku.games.isNotEmpty()) {
                                        sudokuViewModel.currentGame = sudoku.games[0]
                                        navController.navigate(AppAdditionalDestinations.SUDOKU.route)
                                    }
                                },
                                onLongClick = {
                                    scope.launch(Dispatchers.Main) {
                                        val bitmap =
                                            createBitmapFromSudoku(context, sudoku.games[0])
                                        activity?.let { GalleryManager.printBitmap(it, bitmap) }
                                    }
                                },
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.ink_eraser_24px),
                                        contentDescription = "Sudoku Preview",
                                    )
                                },
                                trailingContent = {
                                    Icon(
                                        painterResource(R.drawable.outline_play_arrow),
                                        contentDescription = "Play"
                                    )
                                },
                                overlineContent = {
                                    Text(sudoku.author)
                                },
                                contentPadding = PaddingValues(5.dp)
                            ) {
                                Text(sudoku.name, overflow = TextOverflow.Ellipsis, maxLines = 1)
                            }
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = {
                    importLauncher.launch(
                        arrayOf("application/xml", "text/xml", "application/opensudoku")
                    )
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 22.dp),
            ) {
                Icon(
                    painterResource(R.drawable.outline_upload_file_24),
                    contentDescription = "Import",
                )
            }
        }
    }
}