
package app.myhtl.betala.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import app.myhtl.betala.ui.theme.BetalaTheme
import app.myhtl.betala.R

@Composable
fun MainHeader(modifier: Modifier = Modifier) {
    Text(
        text = "Betala",
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 100.sp
    )
}

@Composable
fun Header(modifier: Modifier = Modifier, text: String, returnDest: String, navController: NavController, menuItems:  @Composable (ColumnScope.() -> Unit)) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { navController.navigate(returnDest) },
            shapes = IconButtonDefaults.shapes(),
        ) {
            Icon(painterResource(R.drawable.close), "Exit")
        }
        Text(
            text = text,
            fontSize = 32.sp,
            fontWeight = FontWeight(350)
        )
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
        ) {
            IconButton(
                onClick = { expanded = !expanded },
                shapes = IconButtonDefaults.shapes()
            ) {
                Icon(painterResource(R.drawable.more_vert), "Dropdown")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                content = menuItems
            )
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.greeting),
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 20.sp,
        modifier = modifier.padding(10.dp),
    )
}

@Composable
fun Donate(context: Context) {
    Column(
        Modifier.padding(horizontal = 50.dp, vertical = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = stringResource(R.string.donate_text),
            fontSize = 15.sp,
            modifier = Modifier.padding(20.dp)
        )
        Button(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            onClick = {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://buymeacoffee.com/".toUri()
                    ), null)
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text(
                text = stringResource(R.string.donate),
                fontSize = 25.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BetalaTheme {
        Greeting()
    }
}