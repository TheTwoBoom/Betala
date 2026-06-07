package app.myhtl.betala.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.myhtl.betala.R
import androidx.compose.ui.res.painterResource

@Composable
fun SudokuScreen(navController: NavController){
    var row_count = 9
    var cells = List(81){1}
    var column_count = 9

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(Modifier
            .fillMaxSize()
            .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally) {

            TopRow(navController)
            CreateSudoku(Modifier.padding(top = 20.dp),row_count, cells)

        }
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
fun CreateSudoku(modifier: Modifier,row_count: Int, cells: List<Int>){
    LazyVerticalGrid(
        modifier = modifier
            .padding(10.dp)
            .background(MaterialTheme.colorScheme.primary)
            .border(width = 4.dp, color = MaterialTheme.colorScheme.primary),
        columns = GridCells.Fixed(row_count)
    ){
        items(cells) { value ->
            SudokuCell(value)
        }
    }
}

@Composable
fun SudokuCell(value: Int){
    val column = value/9
    val row = value%9

    val bigGridLine_vertical = if (column%3 == 0) 3.dp else 1.dp
    val bigGridLine_horizontal = if(row%3 == 0) 3.dp else 1.dp

    Box(modifier = Modifier
        .aspectRatio(1f)
        .padding(1.dp)
        .background(MaterialTheme.colorScheme.tertiary)
        .drawBehind {
            drawLine(
                color = Color.Black,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = bigGridLine_horizontal.toPx()
            )

            drawLine(
                color = Color.Black,
                start = Offset(0f, 0f),
                end = Offset(size.height, 0f),
                strokeWidth = bigGridLine_vertical.toPx()
            )

        },
        contentAlignment = Alignment.Center
    ){
        Text(value.toString())
    }
}