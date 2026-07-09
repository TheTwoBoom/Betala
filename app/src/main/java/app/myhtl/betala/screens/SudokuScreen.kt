package app.myhtl.betala.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.myhtl.betala.R
import androidx.compose.ui.res.painterResource
import app.myhtl.betala.AppAdditionalDestinations
import app.myhtl.betala.SudokuViewModel
import kotlin.math.sqrt


data class SudokuActions(
    val setIndex: (Int) -> Unit,
    val onNumberSelected: (Int) -> Unit,
    val toggleNoteMode: () -> Unit,
    val validate: (Int) -> Boolean,
    val isEditable: (Int) -> Boolean,
    val sameValue: (Int) -> Boolean,
    val isNoteMode: Boolean,
    val erase: () -> Unit,
    val isFinishedAndCorrect: Boolean,
    val getNumbers: Int,
    val isPrinting: Boolean
)
@OptIn(ExperimentalFlexBoxApi::class)
@Composable
fun SudokuScreen(navController: NavController, sudokuViewModel: SudokuViewModel){

    sudokuViewModel.updateIsFinishedAndCorrect()
    LaunchedEffect(sudokuViewModel.isFinishedAndCorrect) {
        if (sudokuViewModel.isFinishedAndCorrect) {
            navController.navigate(AppAdditionalDestinations.WINSCREEN.route) {
                popUpTo(AppAdditionalDestinations.GALLERY.route) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    val sudokugame = sudokuViewModel.currentGame?: return
    val rowCount = sudokugame.getNumSet().size
    val columnCount = sudokugame.getNumSet().size
    val cells = sudokugame.data
    val cellNotes = sudokugame.noteData
    val context = LocalContext.current
    val activity = context as? Activity

    val actions = SudokuActions(
        setIndex = {sudokuViewModel.setIndex(it)},
        onNumberSelected = {sudokuViewModel.onNumberSelected(it)},
        toggleNoteMode = {sudokuViewModel.toggleNoteMode()},
        validate = {sudokuViewModel.validateSudoku(it)},
        isEditable = {sudokuViewModel.isEditable(it)},
        sameValue = {sudokuViewModel.sameValue(it)},
        isNoteMode = sudokuViewModel.isNoteMode,
        erase = {sudokuViewModel.eraseCell()},
        isFinishedAndCorrect = sudokuViewModel.isFinishedAndCorrect,
        getNumbers = sudokugame.getNumSet().size,
        isPrinting = false
    )


    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            if(CurrentDevice.windowSizeClass() == CurrentDevice.MOBILE_PORTRAIT){
            Column(Modifier
                .padding(innerPadding)
                .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally) {

                TopRow(navController)
                CreateSudoku(
                    Modifier.padding(top = 20.dp),
                    rowCount = rowCount,
                    cells = cells,
                    cellNotes = cellNotes,
                    actions = actions,
                    sudokuViewModel.selectedIndex
                )
                NumRow(
                    modifier = Modifier.padding(top = 20.dp),
                    numbers = sudokugame.getNumSet(),
                    actions = actions
                )
                SudokuToolBar(Modifier.padding(top = 10.dp), actions)
            }

            }else{
                Row(Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                    //TopRow(navController)
                    SudokuToolBar(modifier = Modifier, actions)
                    NumRow(
                        modifier = Modifier,
                        numbers = sudokugame.getNumSet(),
                        actions = actions
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    CreateSudoku(
                        Modifier,
                        rowCount = rowCount,
                        cells = cells,
                        cellNotes = cellNotes,
                        actions = actions,
                        sudokuViewModel.selectedIndex
                    )
                }

            }
    }


       /* var bannerAdState by remember { mutableStateOf<BannerAd?>(null) }
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            bannerAdState?.let { bannerAd ->
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AndroidView(
                        modifier = Modifier.wrapContentSize(),
                        factory = { ctx ->
                            activity?.let { bannerAd.getView(it) } ?: View(ctx)
                        },
                    )
                }
            }
        }
        val adSize = AdSize.MEDIUM_RECTANGLE

        // Load the ad when the screen is active.
        val coroutineScope = rememberCoroutineScope()
        val isPreviewMode = LocalInspectionMode.current
        LaunchedEffect(context) {
            bannerAdState?.destroy()
            if (!isPreviewMode) {
                coroutineScope.launch {
                    when (val result = BannerAd.load(BannerAdRequest.Builder("ca-app-pub-3940256099942544/9214589741", adSize).build())) {
                        is AdLoadResult.Success -> {
                            bannerAdState = result.ad
                        }
                        is AdLoadResult.Failure -> {
                            Log.e(null, "Banner ad failed to load: $result.error.message")
                        }
                    }
                }
            }
        }*/


}

@Composable
fun TopRow(navController: NavController){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 25.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(
            onClick = {
                navController.navigate("home")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp)
            )
        }
        Text(text = "Lvl1")//Text muss später ersetzt werden

        Button(onClick = {}) {
            Text("more")
        }
    }
}

@Composable
fun CreateSudoku(modifier: Modifier, rowCount: Int, cells: List<Int>, cellNotes: List<BooleanArray>, actions: SudokuActions, selectedCell: Int){
        LazyVerticalGrid(
            modifier = if(CurrentDevice.windowSizeClass() == CurrentDevice.MOBILE_PORTRAIT){
                modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
            }else{
                modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
            },
            columns = GridCells.Fixed(rowCount),
            userScrollEnabled = false
        ) {
            itemsIndexed(cells) { index, value ->
                val color = calcColor(selectedCell, index, actions = actions)
                val textColor =
                    if (actions.isEditable(index))
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.primary
                SudokuCell(
                    value = value,
                    cellNotes = cellNotes[index],
                    i = index,
                    actions = actions,
                    color = color,
                    textColor = textColor
                ) //textcolor = CalcTextColor)
            }
        }

}

@Composable
fun calcColor(selectedCell: Int, index: Int, actions: SudokuActions): Color{
    val numbers = actions.getNumbers
    val numbersSqrt = sqrt(numbers.toDouble()).toInt()

    if (actions.isPrinting) {
        return Color(0xFFFFFFFF)
    }

    //wrong number?
    if(!actions.validate(index)){
        return MaterialTheme.colorScheme.errorContainer
    }
    //selected?
    if (selectedCell == index) return MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
    //same number as selected?
    else if(actions.sameValue(index)){
        return MaterialTheme.colorScheme.tertiaryContainer
    }
    //same row or column?
    else if(selectedCell/numbers == index/numbers || selectedCell%numbers == index%numbers){
        return MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
    }
    // Subgrid?
    else if (((index/numbers)/numbersSqrt == (selectedCell/numbers)/numbersSqrt) && (index%numbers)/numbersSqrt == (selectedCell%numbers)/numbersSqrt){
        return MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
    }
    //normal Cell
    else{
        return MaterialTheme.colorScheme.surface
    }
}


@Composable
fun SudokuCell(value: Int, cellNotes: BooleanArray, i: Int, actions: SudokuActions, color:Color, textColor: Color) {
    val numbers = actions.getNumbers
    val numbersSqrt = sqrt(numbers.toDouble()).toInt()

    val column = i / numbers
    val row = i % numbers

    val bigGridLineVertical = if (row == 0) 0.dp else if (row % numbersSqrt == 0) 3.dp else 1.dp
    val bigGridLineHorizontal = if (column == 0) 0.dp else if (column % numbersSqrt == 0) 3.dp else 1.dp

    val bigGridLineColor = MaterialTheme.colorScheme.primary


    BoxWithConstraints(
        modifier = Modifier
        .fillMaxSize()
        .background(color = color)
        .aspectRatio(1f)
        .clickable {
            actions.setIndex(i)
        }
        .drawBehind {
            if (bigGridLineHorizontal > 0.dp) {
                drawLine(
                    color = bigGridLineColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = bigGridLineHorizontal.toPx()
                )
            }
            if (bigGridLineVertical > 0.dp) {
                drawLine(
                    color = bigGridLineColor,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = bigGridLineVertical.toPx()
                )
            }

        },
        contentAlignment = Alignment.Center
    ) {
        var fontSize = with(LocalDensity.current) {
            (maxWidth * 0.6f).toSp()
        }
        var text: String
        if (value != 0) {
            text = value.toString()
            Text(
                text = text,
                fontSize = fontSize,
                color = textColor,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            fontSize = with(LocalDensity.current) {
                (maxWidth * 0.25f).toSp()
            }
            Box(modifier = Modifier.fillMaxSize().padding(5.dp)){
            cellNotes.forEachIndexed {index, show ->
                val textAlignments = arrayOf(Alignment.TopStart,Alignment.TopCenter,Alignment.TopEnd,Alignment.CenterStart,Alignment.Center,Alignment.CenterEnd,Alignment.BottomStart,Alignment.BottomCenter,Alignment.BottomEnd)
                if (show){
                    Text(text = (index+1).toString(),
                        modifier = Modifier.fillMaxSize()
                        .wrapContentSize(textAlignments[index]),
                        fontSize = fontSize,
                        style = LocalTextStyle.current.copy(
                            lineHeight = fontSize,
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false)
                        )
                    )
                }
            }


            }

        }
    }
}

@Composable
fun NumRow(modifier: Modifier, numbers: List<Int>, actions: SudokuActions){
    if(CurrentDevice.windowSizeClass() == CurrentDevice.MOBILE_PORTRAIT) {
        Row(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .border(
                    width = 4.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            numbers.forEach { value ->

                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clickable {
                            actions.onNumberSelected(value)
                        }, contentAlignment = Alignment.Center
                ) {
                    val fontSize = with(LocalDensity.current) {
                        if (actions.isNoteMode) {
                            (maxWidth * 0.35f).toSp()
                        } else {
                            (maxWidth * 0.6f).toSp()
                        }
                    }
                    Text(
                        value.toString(),
                        fontSize = fontSize,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    } else{
        Column(
            modifier = modifier
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .border(
                    width = 4.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            numbers.forEach { value ->

                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clickable {
                            actions.onNumberSelected(value)
                        }, contentAlignment = Alignment.Center
                ) {
                    val fontSize = with(LocalDensity.current) {
                        if (actions.isNoteMode) {
                            (maxWidth * 0.35f).toSp()
                        } else {
                            (maxWidth * 0.6f).toSp()
                        }
                    }
                    Text(
                        value.toString(),
                        fontSize = fontSize,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

@Composable
fun SudokuToolBar(modifier :Modifier, actions: SudokuActions){
    if(CurrentDevice.windowSizeClass() == CurrentDevice.MOBILE_PORTRAIT) {
        Row(
            modifier = modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .background(color = MaterialTheme.colorScheme.tertiaryContainer),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    actions.toggleNoteMode()
                }
            ) {
                Icon(

                    painter = if (actions.isNoteMode) {
                        painterResource(id = R.drawable.edit)
                    } else {
                        painterResource(id = R.drawable.edit_off)
                    },
                    contentDescription = "Notes"
                )
            }

            IconButton(
                onClick = {
                    actions.erase()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ink_eraser),
                    contentDescription = "Erase"
                )
            }
        }
    } else{
        Column(
            modifier = modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .background(color = MaterialTheme.colorScheme.tertiaryContainer),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    actions.toggleNoteMode()
                }
            ) {
                Icon(

                    painter = if (actions.isNoteMode) {
                        painterResource(id = R.drawable.edit)
                    } else {
                        painterResource(id = R.drawable.edit_off)
                    },
                    contentDescription = "Notes"
                )
            }

            IconButton(
                onClick = {
                    actions.erase()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ink_eraser),
                    contentDescription = "Erase"
                )
            }
        }
    }
}