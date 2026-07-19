package app.myhtl.betala.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.myhtl.betala.AppAdditionalDestinations
import app.myhtl.betala.AppDestinations
import app.myhtl.betala.R
import app.myhtl.betala.SudokuViewModel
import app.myhtl.betala.opensudoku.GalleryManager
import app.myhtl.betala.opensudoku.GalleryManager.createBitmapFromSudoku
import app.myhtl.betala.opensudoku.GalleryManager.generateAuthorFilters
import app.myhtl.betala.utils.FilterEntry
import app.myhtl.betala.utils.FilterOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GalleryScreen(navController: NavController, sudokuViewModel: SudokuViewModel){
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val filters: SnapshotStateList<FilterOption> = remember {
        mutableStateListOf(
            FilterOption("favorite", "Favorites", mutableStateListOf(
                FilterEntry("", "", false),
            )),
            FilterOption("level", "Difficulty", mutableStateListOf(
                FilterEntry("easy", "Easy", false),
                FilterEntry("medium", "Medium", false),
                FilterEntry("hard", "Hard", false)
            )),
            FilterOption("author", "Author", generateAuthorFilters())
        )
    }
    val sudokuList by remember(filters) {
        derivedStateOf {
            GalleryManager.getFilteredSudokus(context, filters)
        }
    }
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

    // ARTapToPlace(sudokuList.first().games.first())
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Header(
                text = stringResource(R.string.gallery_header),
                returnDest = AppDestinations.HOME.route,
                navController = navController,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Start
            ) {
//                IconButton(
//                    onClick = {},
//                    shapes = IconButtonDefaults.shapes(),
//                ) {
//                    Icon(
//                        painterResource(R.drawable.search),
//                        contentDescription = "Search Button"
//                    )
//                }
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (filter in filters) {
                        var expanded by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopStart)

                        ) {
                            FilterChip(
                                onClick = {
                                    if (filter.options.first().id != "") {
                                        expanded = !expanded
                                    } else {
                                        filter.options.first().isSelected =
                                            !filter.options.first().isSelected
                                    }
                                },
                                label = {
                                    val selOptions = filter.options.filter { it.isSelected }
                                    if (selOptions.isNotEmpty() && filter.options.first().id != "") {
                                        var filterString = ""
                                        selOptions.forEach { filterString += ", " + it.label }
                                        Text(filterString.replaceFirst(",", ""))
                                    } else {
                                        Text(filter.displayName)
                                    }
                                },
                                selected = filter.options.any { it.isSelected },
                                leadingIcon = {
                                    if (filter.options.any { it.isSelected }) {
                                        Icon(
                                            painterResource(R.drawable.check),
                                            contentDescription = "Done icon",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                },
                                trailingIcon = {
                                    if (filter.options.first().id != "") {
                                        Icon(
                                            painterResource(R.drawable.arrow_drop_down),
                                            contentDescription = "Open icon",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                }
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                for (option in filter.options) {
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            option.isSelected = !option.isSelected
                                        },
                                        text = { Text(option.label) },
                                        selected = option.isSelected,
                                        shapes = MenuDefaults.itemShapes()
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (GalleryManager.isLoading) {
                Column(
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { LoadingIndicator() }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(top = 0.dp, start = 10.dp, end = 10.dp),
                ) {
                    if (sudokuList.isEmpty()) item { Text("No sudokus found") }
                    items(sudokuList) { sudoku ->
                        OutlinedCard(
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        ) {
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
                                            createBitmapFromSudoku(context, sudoku.games[0].data)
                                        activity?.let { GalleryManager.printBitmap(it, bitmap) }
                                    }
                                },
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.numbers),
                                        contentDescription = "Sudoku Preview",
                                    )
                                },
                                trailingContent = {
                                    Icon(
                                        painterResource(R.drawable.play_arrow),
                                        contentDescription = "Play"
                                    )
                                },
                                overlineContent = {
                                    Text(sudoku.author)
                                },
                                verticalAlignment = Alignment.CenterVertically,
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
                    painterResource(R.drawable.upload_file),
                    contentDescription = "Import",
                )
            }
        }
    }
}