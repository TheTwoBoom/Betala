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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myhtl.betala.R


@Composable
fun SettingsScreen(navController: NavController){
    val settings = listOf(
        "id" to "Setting1",
        "id" to "Setting2",
        "id" to "Setting3",
        "id" to "Setting4",
        "id" to "Setting5",
        "id" to "Setting6",
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
                    val id = setting.first
                    val displayText = setting.second
                    var checked by remember { mutableStateOf(true) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = displayText,
                            fontSize = 22.sp
                        )
                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = !checked
                            }
                        )
                    }
                }
            }
            Donate(context)
        }


    }
}