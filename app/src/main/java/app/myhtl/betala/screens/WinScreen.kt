package app.myhtl.betala.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.FlexAlignContent
import androidx.compose.foundation.layout.FlexAlignItems
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexDirection
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.myhtl.betala.AppDestinations
import app.myhtl.betala.R

@OptIn(ExperimentalFlexBoxApi::class)
@Composable
fun WinScreen(navController: NavController){
    FlexBox(modifier = Modifier.fillMaxSize().padding(24.dp),
        config = {
            wrap(FlexWrap.Wrap)
            direction(FlexDirection.Row)
            alignContent(FlexAlignContent.Center)
            alignItems(FlexAlignItems.Center)
            gap(24.dp)
        }) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = stringResource(R.string.win_text))
            Button(
                onClick = {navController.popBackStack(AppDestinations.HOME.route, false)}
            ) {
                Text(stringResource(R.string.back))
            }

        }
    }

}