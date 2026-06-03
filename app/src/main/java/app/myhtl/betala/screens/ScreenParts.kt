
package app.myhtl.betala.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myhtl.betala.ui.theme.BetalaTheme

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
        text = "Welcome to Betala - Sudoku Variants!",
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 20.sp,
        modifier = modifier.padding(10.dp),
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BetalaTheme {
        Greeting()
    }
}