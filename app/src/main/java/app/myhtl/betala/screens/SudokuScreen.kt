package app.myhtl.betala.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import app.myhtl.betala.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import app.myhtl.betala.opensudoku.GameManager
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadResult
import kotlinx.coroutines.launch

@Composable
fun SudokuScreen(navController: NavController, sudokugame: GameManager.SudokuGame){
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
            CreateSudoku(Modifier.padding(top = 20.dp),rowCount, cells)

        }

        var bannerAdState by remember { mutableStateOf<BannerAd?>(null) }
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            bannerAdState?.let { bannerAd ->
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AndroidView(
                        modifier = Modifier.wrapContentSize(),
                        factory = { ctx ->
                            activity?.let { bannerAd.getView(it) } ?: android.view.View(ctx)
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
        itemsIndexed(cells) {index, value ->
            SudokuCell(value, index)
        }
    }
}

@Composable
fun SudokuCell(value: Int, i: Int){
    var text: String
    if (value == 0) {
        text = ""
    } else {
        text = value.toString()
    }


    val column = i/9
    val row = i%9

    val bigGridLine_vertical = if (row%3 == 0) 5.dp else 1.dp
    val bigGridLine_horizontal = if(column%3 == 0) 5.dp else 1.dp

    Box(modifier = Modifier
        .aspectRatio(1f)
        .clickable {
            //hier weitermachen für Zahlen einsetzten in Kästchen
            print(row+1)
            println(column+1)
        }
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
                end = Offset(0f, size.height),
                strokeWidth = bigGridLine_vertical.toPx()
            )


        },
        contentAlignment = Alignment.Center
    ){
        Text(text)
    }
}