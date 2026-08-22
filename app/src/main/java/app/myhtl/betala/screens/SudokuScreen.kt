package app.myhtl.betala.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.myhtl.betala.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily.Companion.Monospace
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.myhtl.betala.AppAdditionalDestinations
import app.myhtl.betala.SudokuViewModel
import app.myhtl.betala.opensudoku.SudokuSolver
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sqrt


data class SudokuActions(
    val setIndex: (Int) -> Unit = {},
    val addIndex: (Int) -> Unit = {},
    val onNumberSelected: (Int) -> Unit = {},
    val toggleNoteMode: () -> Unit = {},
    val hasError: (Int) -> Boolean = { false },
    val isEditable: (Int) -> Boolean = { false },
    val originalNumbers: List<Boolean> = emptyList(),
    val sameValue: (Int) -> Boolean = { false },
    val isNoteMode: Boolean = false,
    val erase: () -> Unit = {},
    val isFinishedAndCorrect: Boolean = true,
    val getNumbers: Int,
    val getBoxHeight: Int = sqrt(getNumbers.toDouble()).toInt(),
    val getBoxWidth: Int = sqrt(getNumbers.toDouble()).toInt(),
    val lives: Int = 0,
    val getFinishedNumbers: () -> BooleanArray = {BooleanArray(getNumbers){false}}
)

data class TimerActions(
    val onPauseTimer: () -> Unit = { },
    val onExitScreen: () -> Unit,
    val timerIsRunning: Boolean
)


@Composable
fun SudokuScreen(navController: NavController, sudokuViewModel: SudokuViewModel){

    sudokuViewModel.updateIsFinishedAndCorrect()
    LaunchedEffect(sudokuViewModel.isFinishedAndCorrect) {
        if (sudokuViewModel.isFinishedAndCorrect) {
            if(sudokuViewModel.isRunning.value) sudokuViewModel.pauseOrResumeTimer()
            navController.navigate(AppAdditionalDestinations.WINSCREEN.route)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                if (sudokuViewModel.isRunning.value) {
                    sudokuViewModel.pauseOrResumeTimer()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler(enabled = true) {
        sudokuViewModel.leaveGame()
        navController.popBackStack()
    }

    val sudokuGame = sudokuViewModel.currentGame?: return
    val rowCount = sudokuGame.size
    val columnCount = sudokuGame.size
    val cells = sudokuGame.data
    val cellNotes = sudokuGame.noteData

    val actions = SudokuActions(
        setIndex = {sudokuViewModel.setIndex(it)},
        addIndex = {sudokuViewModel.addIndex(it)},
        onNumberSelected = {sudokuViewModel.onNumberSelected(it)},
        toggleNoteMode = {sudokuViewModel.toggleNoteMode()},
        hasError = {sudokuViewModel.hasError(it)},
        isEditable = {sudokuViewModel.isEditable(it)},
        originalNumbers = sudokuGame.originalList.map { it != 0 },
        sameValue = {sudokuViewModel.sameValue(it)},
        isNoteMode = sudokuViewModel.isNoteMode,
        erase = {sudokuViewModel.eraseCell()},
        isFinishedAndCorrect = sudokuViewModel.isFinishedAndCorrect,
        getNumbers = sudokuGame.size,
        getBoxWidth = sudokuGame.boxWidth,
        getBoxHeight = sudokuGame.boxHeight,
        lives = sudokuViewModel.lifeCount,
        getFinishedNumbers = {sudokuViewModel.finishedNumbers()}
    )

    //timer
    val totalSeconds by sudokuViewModel.seconds.collectAsStateWithLifecycle()
    val isRunning by sudokuViewModel.isRunning.collectAsStateWithLifecycle()

    val timerActions = TimerActions(
        onPauseTimer = { sudokuViewModel.pauseOrResumeTimer() },
        onExitScreen = { sudokuViewModel.leaveGame() },
        timerIsRunning = isRunning
    )



    val minutes = totalSeconds/60
    val seconds = totalSeconds%60
    val time = String.format("%02d:%02d", minutes,seconds)

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (CurrentDevice.windowSizeClass() == CurrentDevice.MOBILE_PORTRAIT) {
            Column(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TopRow(navController, sudokuGame.name, modifier = Modifier.padding(vertical = 10.dp, horizontal = 5.dp), timer = time, timerActions = timerActions)
                SecondTopRow(
                    lives = sudokuViewModel.lifeCount,
                    difficulty = sudokuViewModel.difficulty.label,
                    sudokuSize = sudokuGame.size,
                    sudokuVariant = sudokuViewModel.variant.icon
                )
                if(timerActions.timerIsRunning) {
                    SudokuCanvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        cells = cells,
                        cellNotes = cellNotes,
                        actions = actions,
                        selectedCell = sudokuViewModel.selectedIndex,
                        selectedCells = sudokuViewModel.selectedIndices,
                    )
                }else{
                    EmptySudokuCanvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        actions = actions,
                        timerActions = timerActions
                    )
                }
                Row{
                SudokuToolBar(Modifier.padding(top = 10.dp), actions)
                    //temporary solveButton
                Button(
                    onClick = {
                        val newData = Array(columnCount) { IntArray(rowCount) }
                        for(i in 0 until sudokuGame.data.size){
                            val row = i/rowCount
                            val column = i%columnCount
                            newData[row][column] = sudokuGame.data[i]
                        }
                        //change to data
                        val so = SudokuSolver(inputData = newData, solveOnInit = true, boxWidth = sudokuGame.boxWidth, boxHeight = sudokuGame.boxHeight)

                        val d = so.data
                        for(i in 0 until sudokuGame.data.size){
                            val row = i/rowCount
                            val column = i%columnCount
                            actions.setIndex(i)
                            actions.onNumberSelected(d[row][column])
                        }
                            actions.toggleNoteMode()

                        val n = so.notes

                        for(i in 0 until sudokuGame.data.size){
                            val row = i/rowCount
                            val column = i%columnCount
                            actions.setIndex(i)
                            for(j in 0 until rowCount){
                                if(n[row][column][j]){
                                    actions.onNumberSelected(j+1)
                                }

                            }
                        }

                        actions.toggleNoteMode()
                    }
                ){
                    Text("Solve")
                }
            }
                NumRow(
                    modifier = Modifier.padding(top = 20.dp),
                    numbers = (1 .. sudokuGame.size).map { it },
                    actions = actions
                )
            }
            }else{
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                )
                {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        TopRow(
                            navController = navController,
                            name = sudokuGame.name,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(horizontal = 5.dp),
                            timerActions = timerActions
                        )
                        SecondTopRow(
                            lives = sudokuViewModel.lifeCount,
                            difficulty = sudokuViewModel.difficulty.label,
                            sudokuSize = sudokuGame.size,
                            sudokuVariant = sudokuViewModel.variant.icon
                        )

                        Timer(timer = time, timerActions = timerActions)
                        Spacer(Modifier.size(10.dp))
                        SudokuToolBar(modifier = Modifier, actions)
                        NumRow(
                            modifier = Modifier,
                            numbers = (1 .. sudokuGame.size).map { it },
                            actions = actions
                        )
                        Spacer(Modifier.size(10.dp))
                        }

                    if (timerActions.timerIsRunning) {
                        SudokuCanvas(
                            Modifier
                                .fillMaxHeight()
                                .padding(vertical = 10.dp)
                                .padding(end = 20.dp),
                            cells = cells,
                            cellNotes = cellNotes,
                            actions = actions,
                            selectedCell = sudokuViewModel.selectedIndex,
                            selectedCells = sudokuViewModel.selectedIndices,
                        )
                    }
                    else{
                        EmptySudokuCanvas(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 10.dp)
                                .padding(end = 20.dp),
                            actions = actions,
                            timerActions = timerActions
                        )
                    }
                }

            }
    }
}

@Composable
fun TopRow(navController: NavController, name: String, timer: String = "", timerActions: TimerActions, modifier: Modifier = Modifier){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        IconButton(
            onClick = {
                timerActions.onExitScreen()
                navController.popBackStack()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )
        }

        Text(text = name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight(350),
            modifier = Modifier.weight(1f)
            )

        //for timer
        if(timer.isNotEmpty()){
            Timer(timer = timer, timerActions = timerActions)
        }

        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            IconButton(
                onClick = { expanded = !expanded },
                shapes = IconButtonDefaults.shapes()
            ) {
                Icon(painterResource(R.drawable.more_vert), "Dropdown")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                content = {
                    DropdownMenuItem(
                        text = { Text("Placeholder") },
                        onClick = {}
                    )
                }
            )
        }


    }
}

@Composable
fun Timer(timer: String, timerActions: TimerActions){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ){
        /*Icon(
            painter = painterResource(id = R.drawable.timer),
            contentDescription = "Timer",
            modifier = Modifier.size(20.dp)
        )*/
        Text(text = timer, fontFamily = Monospace )
        IconButton(
            modifier = Modifier.size(25.dp),
            onClick = {
                timerActions.onPauseTimer()
            }
        ) {
            if(timerActions.timerIsRunning){
                Icon(
                    painter = painterResource(id = R.drawable.pause),
                    contentDescription = "Pause",
                    modifier = Modifier.size(20.dp)
                )
            }else{
                Icon(
                    painter = painterResource(id = R.drawable.resume),
                    contentDescription = "Start",
                    modifier = Modifier.size(20.dp)
                )
            }

        }

    }
}

@Composable
fun SecondTopRow(lives: Int, difficulty: Int, sudokuSize: Int, modifier: Modifier = Modifier, sudokuVariant: Int){
    val primaryColor = MaterialTheme.colorScheme.primary
    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row{
            Icon(
                painter = painterResource(id = R.drawable.heart_filled),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "Lives",
                modifier = Modifier.size(20.dp)
            )
            Text(" X $lives", color = primaryColor)
        }

        Text(stringResource(difficulty), color = primaryColor)
        Text("$sudokuSize x $sudokuSize", color = primaryColor)
        Icon(
            painter = painterResource(id = sudokuVariant),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "VariantIcon",
            modifier = Modifier.size(20.dp)
        )
    }

}

@Composable
fun EmptySudokuCanvas(
    modifier: Modifier = Modifier,
    actions: SudokuActions,
    timerActions: TimerActions
){
    val numbers = actions.getNumbers
    val boxWidth = actions.getBoxWidth
    val boxHeight = actions.getBoxHeight

    val lineColor = MaterialTheme.colorScheme.primary
    val iconColor = MaterialTheme.colorScheme.primary
    val iconBackgroundColor = MaterialTheme.colorScheme.surfaceContainer

    val iconPainter = painterResource(R.drawable.resume)
    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        timerActions.onPauseTimer()
                    }
                )
            }
    ) {


        val cellSize = size.width / numbers

        for(i in 1..numbers-1){
            val thicknessX = if(i%boxWidth == 0) 2.dp else 0.5.dp
            val thicknessY = if(i%boxHeight == 0) 2.dp else 0.5.dp
            val position = i*cellSize

            drawLine(
                color = lineColor,
                start = Offset(position, 0f),
                end = Offset(position, size.height),
                strokeWidth = thicknessX.toPx()
            )

            drawLine(
                color = lineColor,
                start = Offset(0f, position),
                end = Offset(size.width, position),
                strokeWidth = thicknessY.toPx()
            )
        }


        val iconSize = Size(100.dp.toPx(), 100.dp.toPx())
        val x = (size.width - iconSize.width) / 2
        val y = (size.height - iconSize.height) / 2

        drawCircle(radius = 150f, color = iconBackgroundColor, center = Offset(size.width/2, size.height/2))
        translate(left = x, top = y) {
            with(iconPainter) {
                draw(
                    size = iconSize,
                    colorFilter = ColorFilter.tint(iconColor)
                )
            }
        }

    }
}
@Composable
fun SudokuCanvas(
    modifier: Modifier = Modifier,
    cells: List<Int>,
    cellNotes: List<BooleanArray>,
    actions: SudokuActions,
    selectedCell: Int,
    selectedCells: Set<Int>
){
    val textMeasurer = rememberTextMeasurer()
    val numbers = actions.getNumbers
    val boxWidth = actions.getBoxWidth
    val boxHeight = actions.getBoxHeight


    val originalNumbers = actions.originalNumbers

    val colors = object {
        val lineColor = MaterialTheme.colorScheme.primary
        val onSurface = MaterialTheme.colorScheme.onSurface
        val errorColor = MaterialTheme.colorScheme.errorContainer
        val highlightColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
        val selectedColor = MaterialTheme.colorScheme.secondaryContainer
        val sameValueColor = MaterialTheme.colorScheme.tertiaryContainer
    }

    fun getIndexWithOffset(offset: Offset, size: IntSize): Int{
        val cellSize = size.width/numbers
        val col = floor(offset.x/cellSize).toInt()
        val row = floor(offset.y/cellSize).toInt()


        if(col in 0 until numbers && row in 0 until numbers){
            return row*numbers + col
        }
        else{
            return -1
        }
    }

    var lastIndex by remember { mutableIntStateOf(-1) }
    val chars = remember { List(numbers) {it+1}.toChar()}

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            /*.border(
                color = colors.lineColor,
                width = 1.dp
            )*/
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val index = getIndexWithOffset(offset, size)
                        if (index != -1) {
                            actions.setIndex(index)
                            lastIndex = index
                        }
                    },
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        val index = getIndexWithOffset(change.position, size)
                        if (index != -1 && index != lastIndex) {
                            actions.addIndex(index)
                            lastIndex = index
                        }

                    }

                )
            }
    ){
         val cellSize = size.width / numbers

        //cash text for better performance, so it doesn't have to render for each cell
        val mainTextStyle = TextStyle(fontSize = (cellSize * 0.6f).toSp(),
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            textAlign = TextAlign.Center)

        val noteTextStyle = TextStyle(fontSize = (cellSize * 0.28f).toSp(),
            color = colors.onSurface,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            textAlign = TextAlign.Center
        )
        val mainTextCache = List(numbers) { n ->
            textMeasurer.measure((chars[n]).toString(), mainTextStyle)
        }

        val noteTextCache = List(numbers) { n ->
            textMeasurer.measure((chars[n]).toString(), noteTextStyle)
        }

        //Highlighting for multipleCells
        val selection = selectedCells + selectedCell

        val commonRow = selection.map { it / numbers }.distinct().let {
            if (it.size == 1) it.first() else -1
        }

        val commonCol = selection.map { it % numbers }.distinct().let {
            if (it.size == 1) it.first() else -1
        }

        val commonBoxRow = selection.map { (it / numbers) / boxHeight }.distinct().let {
            if (it.size == 1) it.first() else -1
        }
        val commonBoxCol = selection.map { (it % numbers) / boxWidth }.distinct().let {
            if (it.size == 1) it.first() else -1
        }


        for(i in 0 until numbers*numbers){
            val row = i/numbers
            val col = i%numbers
            val bRow = row/boxHeight
            val bCol = col/boxWidth
            //Draw Colors
            val shouldHighlight = (row == commonRow && commonRow != -1) ||
                    (col == commonCol) ||
                    (bRow == commonBoxRow && bCol == commonBoxCol && commonBoxRow != -1)

            val color = when {
                selectedCell == -1 -> null
                actions.hasError(i) -> colors.errorColor
                i == selectedCell || selectedCells.contains(i) -> colors.selectedColor
                actions.sameValue(cells[i]) && selectedCells.isEmpty() -> colors.sameValueColor
                shouldHighlight -> colors.highlightColor
                else -> null
            }

            color?.let {
                drawRect(
                    color = it,
                    topLeft = Offset(col*cellSize, row*cellSize),
                    size = Size(cellSize, cellSize)
                )
            }

            //Draw numbers
            if(cells[i] != 0){
                val color = if(originalNumbers[i]) colors.lineColor else colors.onSurface

                val textLayout = mainTextCache[cells[i] - 1]
                val textOffset = Offset(
                    col * cellSize + (cellSize - textLayout.size.width) / 2,
                    row * cellSize + (cellSize - textLayout.size.height) / 2
                )
                drawText(textLayoutResult = textLayout, topLeft = textOffset, color = color)
            }
            //Draw notes
            else if(cellNotes[i].count{ it } != 0){
                for(j in 0 until numbers){
                    if(cellNotes[i][j]){
                        val textLayout = noteTextCache[j]

                        val subCellWidth = cellSize / boxWidth
                        val subCellHeight = cellSize / boxHeight

                        val subCol = j % boxWidth
                        val subRow = j / boxWidth

                        val centerX = col * cellSize + (subCol * subCellWidth) + (subCellWidth / 2)
                        val centerY = row * cellSize + (subRow * subCellHeight) + (subCellHeight / 2)


                        val textOffset = Offset(
                            x = centerX - (textLayout.size.width / 2),
                            y = centerY - (textLayout.size.height / 2)
                        )

                        // backgroundColor
                        if(selectedCells.isEmpty() && actions.sameValue(j+1)){
                            drawRect(
                                color = colors.sameValueColor,
                                topLeft = Offset(col * cellSize + (subCol * subCellWidth), row * cellSize + (subRow * subCellHeight)),
                                size = Size(subCellWidth, subCellHeight)
                            )
                        }

                        drawText(textLayoutResult = textLayout, topLeft = textOffset)

                    }
                }

            }
        }


        //Draw lines
        for(i in 1..numbers-1){
            val thicknessX = if(i%boxWidth == 0) 2.dp else 0.5.dp
            val thicknessY = if(i%boxHeight == 0) 2.dp else 0.5.dp
            val position = i*cellSize

            drawLine(
                color = colors.lineColor,
                start = Offset(position, 0f),
                end = Offset(position, size.height),
                strokeWidth = thicknessX.toPx()
            )

            drawLine(
                color = colors.lineColor,
                start = Offset(0f, position),
                end = Offset(size.width, position),
                strokeWidth = thicknessY.toPx()
            )
        }

    }


}

@Composable
fun NumRow(numbers: List<Int>, actions: SudokuActions, modifier: Modifier){
    val colors = object {
        val number = MaterialTheme.colorScheme.surface
        val numberFinished = MaterialTheme.colorScheme.onSurfaceVariant
    }



    val finishedNumbers = actions.getFinishedNumbers()


    if(CurrentDevice.windowSizeClass() == CurrentDevice.MOBILE_PORTRAIT) {
        val maxItemsPerRow = 9
        val estimatedRows = (numbers.size + maxItemsPerRow -1) / maxItemsPerRow
        val itemHeight = when (estimatedRows){
            1 -> 50.dp
            2 -> 35.dp
            else -> 28.dp
        }


        val itemsPerRow = (numbers.size + estimatedRows - 1) / estimatedRows

        val increment = if(estimatedRows == 1) 18 - itemsPerRow*2 else 0
        val fontSize = if (actions.isNoteMode) {
            (15+increment/1.5).sp
        } else {
            (30+increment).sp
        }

        val chunks = numbers.chunked(itemsPerRow)
        val chars = numbers.toChar()

        Column(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .heightIn(min = 50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .border(
                    width = 4.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(3.dp)
        ) {
            chunks.forEach { rowNumbers ->
                Row() {
                    rowNumbers.forEach { number ->
                        val color = if (finishedNumbers[number-1]) colors.numberFinished else colors.number

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(itemHeight)
                                .clickable {
                                    actions.onNumberSelected(number)
                                }, contentAlignment = Alignment.Center
                        ) {
                            Text(
                                chars[number - 1].toString(),
                                fontSize = fontSize,
                                color = color
                            )
                        }
                    }
                }

            }
        }
    } else {
        val maxItemsPerRow = sqrt(numbers.size.toDouble()).roundToInt()
        val estimatedRows = (numbers.size + maxItemsPerRow - 1) / maxItemsPerRow
        val itemHeight = when (estimatedRows) {
            1 -> 70.dp
            2 -> 50.dp
            else -> 40.dp
        }

        val itemsPerRow = (numbers.size + estimatedRows - 1) / estimatedRows

        val fontSize = if (actions.isNoteMode) {
            (15).sp
        } else {
            (30).sp
        }

        val chunks = numbers.chunked(itemsPerRow)
        val chars = numbers.toChar()

        Column(
            modifier = modifier
                .fillMaxWidth(0.7f)
                .heightIn(min = 50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .border(
                    width = 4.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(3.dp)
        ) {
            chunks.forEach { rowNumbers ->
                Row() {
                    rowNumbers.forEach { number ->
                        val color = if (finishedNumbers[number-1]) colors.numberFinished else colors.number

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(itemHeight)
                                .clickable {
                                    actions.onNumberSelected(number)
                                }, contentAlignment = Alignment.Center
                        ) {
                            Text(
                                chars[number - 1].toString(),
                                fontSize = fontSize,
                                color = color
                            )
                        }
                    }
                }

            }
        }
    }
}


fun List<Int>.toChar(): List<Char>{
    return this.map { number ->
        if(number in 0..9){
            number.digitToChar()
        } else{
            ('A'.code + number-10).toChar()
        }
    }
}


@Composable
fun SudokuToolBar(modifier :Modifier, actions: SudokuActions){
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

}