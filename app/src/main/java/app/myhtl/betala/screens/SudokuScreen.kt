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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import app.myhtl.betala.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
    val validate: (Int) -> Boolean,
    val isEditable: (Int) -> Boolean
)
@Composable
fun SudokuScreen(navController: NavController, sudokuViewModel: SudokuViewModel){
    val actions = SudokuActions(
        setIndex = {sudokuViewModel.setIndex(it)},
        onNumberSelected = {sudokuViewModel.onNumberSelected(it)},
        validate = {sudokuViewModel.validateSudoku(it)},
        isEditable = {sudokuViewModel.isEditable(it)}
    )


    val sudokugame = sudokuViewModel.currentGame?: return
    val rowCount = 9
    val columnCount = 9
    val cells = sudokugame.data
    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(Modifier
            .fillMaxSize()
            .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally) {

            TopRow(navController)
            CreateSudoku(Modifier.padding(top = 20.dp),
                rowCount,
                cells,
                actions = actions,
                sudokuViewModel.selectedIndex)
            NumRow(
                modifier = Modifier.padding(top = 20.dp),
                numbers = sudokugame.getNumSet(),
                onNumberClick = { number ->
                    //sudokuViewModel.onNumberSelected(number)
                    actions.onNumberSelected(number)

                }
                )

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
fun CreateSudoku(modifier: Modifier,row_count: Int, cells: List<Int>, actions: SudokuActions, selectedCell: Int){
    LazyVerticalGrid(
        modifier = modifier
            .padding(10.dp)
            .border(width = 4.dp, color = MaterialTheme.colorScheme.primary)
        ,
        columns = GridCells.Fixed(row_count)
    ){
        itemsIndexed(cells) {index, value ->
            val color = CalcColor(selectedCell, index, actions = actions)
            val textColor = if(actions.isEditable(index)) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary
            SudokuCell(value, index, setIndex = actions.setIndex, color = color, textColor = textColor ) //textcolor = CalcTextColor)
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
fun SudokuCell(value: Int, i: Int, setIndex: (Int) -> Unit, color:Color, textColor: Color){
    var text: String
    if (value == 0) {
        text = ""
    } else {
        text = value.toString()
    }


    val column = i/9
    val row = i%9

    val bigGridLine_vertical = if(row == 0) 0.dp else if (row%3 == 0) 4.dp else 1.dp
    val bigGridLine_horizontal = if(column == 0) 0.dp else if(column%3 == 0) 4.dp else 1.dp

    val bigGridLine_color = MaterialTheme.colorScheme.primary


    BoxWithConstraints(modifier = Modifier
        .background(color = color)
        .aspectRatio(1f)
        .clickable {
            Log.d("Sudoku", "TEST")
            setIndex(i)
        }
        .drawBehind {
            if(bigGridLine_horizontal>0.dp) {
                drawLine(
                    color = bigGridLine_color,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = bigGridLine_horizontal.toPx()
                )
            }
            if(bigGridLine_vertical>0.dp) {
                drawLine(
                    color = bigGridLine_color,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = bigGridLine_vertical.toPx()
                )
            }

        },
        contentAlignment = Alignment.Center
    ){
        val fontSize = with(LocalDensity.current) {
            (maxWidth * 0.6f).toSp()
        }
        Text(text = text, fontSize = fontSize, color = textColor)
    }
}

@Composable
fun NumRow(modifier: Modifier, numbers: List<Int>, onNumberClick: (Int) -> Unit){

    LazyRow(
        modifier = modifier
            .padding(10.dp)
            .background(MaterialTheme.colorScheme.primary)
            .border(width = 4.dp, color = MaterialTheme.colorScheme.primary)
            .wrapContentWidth()
            .height(35.dp),
    ) {
        items(numbers) { value ->

            Box(
                modifier = Modifier
                .aspectRatio(1f)
                .clickable {
                    Log.d("Numbers", "TEST")
                    onNumberClick(value)
                },contentAlignment = Alignment.Center
            ){
                Text(value.toString())
            }
        }
    }
}