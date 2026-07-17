package app.myhtl.betala.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import app.myhtl.betala.AppAdditionalDestinations
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.myhtl.betala.R
import app.myhtl.betala.SudokuViewModel
import app.myhtl.betala.opensudoku.GalleryManager
import app.myhtl.betala.opensudoku.GameManager
import app.myhtl.betala.opensudoku.SudokuGenerator
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen(navController: NavController, sudokuViewModel: SudokuViewModel){
    val context = LocalContext.current
    val activity = context as? Activity
    var bannerAdState by remember { mutableStateOf<BannerAd?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            text = "Betala",
            navController = navController,
            textComposable = {
                Text(
                    text = "BETALA",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 32.sp,
                    fontFamily = FontFamily(Font(R.font.majormonodisplay)),
                )
            },
            menuItems = {},
            leftButton = {},
            rightButton = {
                IconButton(
                    onClick = {},
                    shapes = IconButtonDefaults.shapes(
                        IconButtonDefaults.largeSquareShape,
                    )
                ) {
                    Icon(painterResource(R.drawable.account_box), "Account")
                }
            }
        )
        SudokuCarousel(
            text = "Sudokus of the Day",
            items = GalleryManager.getAllSudokus(context),
            navController = navController,
            sudokuViewModel = sudokuViewModel,
            isLoading = GalleryManager.isLoading
        )
        OutlinedCard(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 5.dp)
                .clickable(true) {
                    val generator = SudokuGenerator(numbers = 9)
                    // would else be empty
                    var sudoku = GameManager.SudokuGame(generator.getRandomSudoku())
                    sudokuViewModel.currentGame = sudoku;

                    navController.navigate(AppAdditionalDestinations.SUDOKU.route)
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(painterResource(R.drawable.dice), "Dice")
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = stringResource(R.string.play)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedCard(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp)
                    .clickable(true) {
                        navController.navigate(AppAdditionalDestinations.GALLERY.route)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(painterResource(R.drawable.puzzle), "Puzzle")
                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = stringResource(R.string.select_level)
                    )
                }
            }
            OutlinedCard(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp)
                    .clickable(true) {
                        val numbers = 9
                        val sudoku =
                            GameManager.SudokuGame(SnapshotStateList(numbers * numbers) { 0 })
                        sudokuViewModel.currentGame = sudoku;
                        navController.navigate(AppAdditionalDestinations.SUDOKU.route)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(painterResource(R.drawable.note_add), "Add")
                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = stringResource(R.string.new_sudoku)
                    )
                }
            }
        }


        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            bannerAdState?.let { bannerAd ->
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center,
                ) {
                    AndroidView(
                        modifier = Modifier.wrapContentSize(),
                        factory = { ctx ->
                            activity?.let { bannerAd.getView(it) } ?: View(ctx)
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
                    when (val result = BannerAd.load(
                        BannerAdRequest.Builder(
                            "ca-app-pub-3940256099942544/9214589741",
                            adSize
                        ).build()
                    )) {
                        is AdLoadResult.Success -> {
                            bannerAdState = result.ad
                        }

                        is AdLoadResult.Failure -> {
                            val error: String = result.error.message
                            Log.e(null, "Banner ad failed to load: $error")
                        }
                    }
                }
            }
        }
    }
}