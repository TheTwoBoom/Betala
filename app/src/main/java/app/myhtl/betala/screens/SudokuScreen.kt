package app.myhtl.betala.screens

import ads_mobile_sdk.pr
import android.R.attr.maxWidth
import android.app.Activity
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import app.myhtl.betala.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.myhtl.betala.AppAdditionalDestinations
import app.myhtl.betala.SudokuViewModel
import app.myhtl.betala.opensudoku.GameManager
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadResult
import kotlinx.coroutines.launch


data class SudokuActions(
    val setIndex: (Int) -> Unit,
    val onNumberSelected: (Int) -> Unit,
    val toggleNoteMode: () -> Unit,
    val validate: (Int) -> Boolean,
    val isEditable: (Int) -> Boolean,
    val sameValue: (Int) -> Boolean,
    val isNoteMode: Boolean,
    val erase: () -> Unit,
    val isFinishedAndCorrect: Boolean
)
@Composable
fun SudokuScreen(navController: NavController, sudokuViewModel: SudokuViewModel){
    val actions = SudokuActions(
        setIndex = {sudokuViewModel.setIndex(it)},
        onNumberSelected = {sudokuViewModel.onNumberSelected(it)},
        toggleNoteMode = {sudokuViewModel.toggleNoteMode()},
        validate = {sudokuViewModel.validateSudoku(it)},
        isEditable = {sudokuViewModel.isEditable(it)},
        sameValue = {sudokuViewModel.sameValue(it)},
        isNoteMode = sudokuViewModel.isNoteMode,
        erase = {sudokuViewModel.eraseCell()},
        isFinishedAndCorrect = sudokuViewModel.isFinishedAndCorrect
    )

if(actions.isFinishedAndCorrect){
    navController.navigate(AppAdditionalDestinations.WINSCREEN.route)
}

    val sudokugame = sudokuViewModel.currentGame?: return
    val rowCount = sudokugame.getNumSet().size
    val columnCount = 9
    val cells = sudokugame.data
    val cellNotes = sudokugame.noteData
    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(Modifier
            .fillMaxSize()
            .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally) {

            TopRow(navController)
            CreateSudoku(Modifier.padding(top = 20.dp),
                rowCount = rowCount,
                cells = cells,
                cellNotes = cellNotes,
                actions = actions,
                sudokuViewModel.selectedIndex)
            NumRow(
                modifier = Modifier.padding(top = 20.dp),
                numbers = sudokugame.getNumSet(),
                actions = actions
            )
            SudokuToolBar(Modifier.padding(top = 10.dp), actions)
            /*NotesNumRow(
                modifier = Modifier.padding(top = 20.dp),
                numbers = sudokugame.getNumSet(),
                onNoteNumberSelected = { number -> actions.onNoteNumberSelected(number)}
                )*/

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
                navController.popBackStack()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_outline_arrow_back),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp)
            )
        }
        Header(text = "Lvl1")//Text muss später ersetzt werden

        Button(onClick = {}) {
            Text("more")
        }
    }
}

@Composable
fun CreateSudoku(modifier: Modifier, rowCount: Int, cells: List<Int>, cellNotes: List<BooleanArray>, actions: SudokuActions, selectedCell: Int){
    LazyVerticalGrid(
        modifier = modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(width = 4.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
        ,
        columns = GridCells.Fixed(rowCount)
    ){
        itemsIndexed(cells) {index, value ->
            val color = CalcColor(selectedCell, index, actions = actions)
            val textColor = if(actions.isEditable(index)) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary
            SudokuCell(value = value,cellNotes = cellNotes[index], i = index, actions = actions, color = color, textColor = textColor ) //textcolor = CalcTextColor)
        }
    }
}

@Composable
fun CalcColor(selectedCell: Int, index: Int, actions: SudokuActions): Color{
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
    else if(selectedCell/9 == index/9 || selectedCell%9 == index%9){
        return MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
    }
    // Subgrid?
    else if (((index/9)/3 == (selectedCell/9)/3) && (index%9)/3 == (selectedCell%9)/3){
        return MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
    }
    //normal Cell
    else{
        return MaterialTheme.colorScheme.surface
    }
}


@Composable
fun SudokuCell(value: Int, cellNotes: BooleanArray, i: Int, actions: SudokuActions, color:Color, textColor: Color) {


    val column = i / 9
    val row = i % 9

    val bigGridLine_vertical = if (row == 0) 0.dp else if (row % 3 == 0) 3.dp else 1.dp
    val bigGridLine_horizontal = if (column == 0) 0.dp else if (column % 3 == 0) 3.dp else 1.dp

    val bigGridLine_color = MaterialTheme.colorScheme.primary


    BoxWithConstraints(
        modifier = Modifier
        .fillMaxSize()
        .background(color = color)
        .aspectRatio(1f)
        .clickable {
            actions.setIndex(i)
        }
        .drawBehind {
            if (bigGridLine_horizontal > 0.dp) {
                drawLine(
                    color = bigGridLine_color,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = bigGridLine_horizontal.toPx()
                )
            }
            if (bigGridLine_vertical > 0.dp) {
                drawLine(
                    color = bigGridLine_color,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = bigGridLine_vertical.toPx()
                )
            }

        },
        contentAlignment = Alignment.Center
    ) {
        var fontSize = with(LocalDensity.current) {
            (maxWidth * 0.6f).toSp()
        }
        var text = ""
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
                    },contentAlignment = Alignment.Center
            ){
                val fontSize = with(LocalDensity.current) {
                    if(actions.isNoteMode){
                        (maxWidth * 0.35f).toSp()
                    }
                    else{
                        (maxWidth * 0.6f).toSp()
                    }
                }
                Text(value.toString(), fontSize = fontSize, color = MaterialTheme.colorScheme.surface)
            }
        }
    }
}

@Composable
fun SudokuToolBar(modifier :Modifier, actions: SudokuActions){
    Row(modifier = modifier
        .clip(shape = RoundedCornerShape(12.dp))
        .background(color = MaterialTheme.colorScheme.tertiaryContainer),
        verticalAlignment = Alignment.CenterVertically){
        IconButton(
            onClick = {
                actions.toggleNoteMode()
            }
        ) {
            Icon(

                painter = if(actions.isNoteMode){
                    painterResource(id = R.drawable.edit_24px)
                } else {painterResource(id = R.drawable.edit_off_24px)},
                contentDescription = "Notes"
            )
        }

        IconButton(
            onClick = {
                actions.erase()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ink_eraser_24px),
                contentDescription = "Erase"
            )
        }
    }
}