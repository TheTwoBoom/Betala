package app.myhtl.betala.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.myhtl.betala.AppMainDestinations
import app.myhtl.betala.R
import app.myhtl.betala.SudokuViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp

@Composable
fun WinScreen(navController: NavController, sudokuViewModel: SudokuViewModel){
    val containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.2f)

    val containerModifier = Modifier
        .fillMaxWidth(0.8f)
        .clip(RoundedCornerShape(15.dp))
        .background(containerColor)
        .padding(15.dp)


    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
        Text(modifier = Modifier.padding(bottom = 20.dp),text = stringResource(R.string.win_text), fontSize = 25.sp)
        //TODO() change when sudokus can be saved!
        val totalSeconds = sudokuViewModel.seconds.collectAsState().value

        val minutes = totalSeconds/60
        val seconds = totalSeconds%60
        val time = if(minutes == 0) String.format("%ds ", seconds) else String.format("%dm %ds ", minutes,seconds)

        Column(
            modifier = containerModifier
        ) {
            StatisticRow(
                stringResource(R.string.winscreen_time_text),
                time
            )
            StatisticRow(
                stringResource(R.string.winscreen_mistakes_text),
                ""+(3 - sudokuViewModel.lifeCount)
            )

        }

        val game = sudokuViewModel.currentGame ?: return

        Column(
            modifier = containerModifier
        ) {
            StatisticRow(
                stringResource(R.string.selectSize_text),
                ""+game.size + "*" + game.size + " (" + game.boxWidth + "*" + game.boxHeight + ")"
            )

            StatisticRow(
                stringResource(R.string.selectDifficulty_text),
                ""+sudokuViewModel.difficulty
            )

            StatisticRow(
                stringResource(R.string.selectVariants_text),
                sudokuViewModel.variant.icon,
                "VariantIcon"
            )
        }

        val actions = SudokuActions(
            getNumbers = game.size,
            getBoxWidth = game.boxWidth,
            getBoxHeight = game.boxHeight,
            originalNumbers = game.originalList.map { it != 0 }
        )

        SudokuCanvas(
            modifier = containerModifier,
            cells = game.data,
            cellNotes = listOf(BooleanArray(0)),
            actions = actions,
            selectedCell = -1,
            selectedCells = emptySet(),
            isStatic = true
        )

        Button(
            onClick = {
                sudokuViewModel.leaveGame()
                navController.popBackStack(AppMainDestinations.HOME.route, false)
            }
        ) {
            Text(stringResource(R.string.back))
        }

    }
}

@Composable
fun StatisticRow(text1: String, text2: String){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text1, color = MaterialTheme.colorScheme.primary)
        Text(text2)
    }
}

@Composable
fun StatisticRow(text: String, iconId: Int, iconDescription: String){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text, color = MaterialTheme.colorScheme.primary)
        Icon(
            painter = painterResource(iconId),
            contentDescription = iconDescription,
            modifier = Modifier.size(20.dp)
        )
    }
}