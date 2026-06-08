
package app.myhtl.betala.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
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
fun Header(modifier: Modifier = Modifier, text:String) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 35.sp
    )

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