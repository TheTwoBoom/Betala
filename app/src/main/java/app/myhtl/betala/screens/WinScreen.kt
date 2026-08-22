package app.myhtl.betala.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.FlexAlignContent
import androidx.compose.foundation.layout.FlexAlignItems
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexDirection
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WinScreen(navController: NavController, sudokuViewModel: SudokuViewModel){
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = stringResource(R.string.win_text))
        //TODO() change when sudokus can be saved!
        val totalSeconds = sudokuViewModel.seconds.collectAsState().value

        val minutes = totalSeconds/60
        val seconds = totalSeconds%60
        val time = if(minutes == 0) String.format("%ds ", seconds) else String.format("%dm %ds ", minutes,seconds)

        Spacer(Modifier.size(30.dp))
        Text(stringResource(R.string.winscreen_time_text) + time)

        Text(stringResource(R.string.winscreen_mistakes_text) + (3 - sudokuViewModel.lifeCount))
        Spacer(Modifier.size(30.dp))

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