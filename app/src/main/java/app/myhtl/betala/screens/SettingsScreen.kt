package app.myhtl.betala.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myhtl.betala.R
import app.myhtl.betala.utils.SettingUtils


@Composable
fun SettingsScreen(navController: NavController){
    val settings = listOf(
        "darkmode" to "Force Dark mode",
        "notemode" to "Disable Note mode",
        "validate" to "Disable Sudoku Validation",
    )
    val context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Header(
                text = "Settings",
                navController = navController,
                leftButton = {
                    IconButton({}, IconButtonDefaults.shapes(), Modifier.visible(false)) {
                        Icon(painterResource(R.drawable.close),"")
                    }
                },
            )
            LazyColumn(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(15.dp)
            ){
                items(settings) { setting ->
                    val utils = SettingUtils(context)
                    val id = setting.first
                    val displayText = setting.second
                    var checked by remember { mutableStateOf(utils.getBool(id)!!) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayText,
                            fontSize = 22.sp
                        )
                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = !checked
                                utils.setBool(id, checked)
                            }
                        )
                    }
                }
            }
            Text(
                text = "Betala v0.0.1 \n" +
                        "Copyright © 2026 by the Betala Developers",
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
            //Donate(context)
        }


    }
}