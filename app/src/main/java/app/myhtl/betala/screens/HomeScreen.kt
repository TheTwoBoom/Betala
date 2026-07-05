package app.myhtl.betala.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.FlexAlignContent
import androidx.compose.foundation.layout.FlexAlignItems
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexDirection
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.navigation.NavController
import app.myhtl.betala.AppAdditionalDestinations
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import app.myhtl.betala.R
import app.myhtl.betala.SudokuViewModel
import app.myhtl.betala.opensudoku.GameManager
import app.myhtl.betala.opensudoku.SudokuGenerator
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalFlexBoxApi::class)
@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen(navController: NavController, sudokuViewModel: SudokuViewModel){
    val context = LocalContext.current
    val activity = context as? Activity
    var bannerAdState by remember { mutableStateOf<BannerAd?>(null) }

    FlexBox(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        config = {
            wrap(FlexWrap.Wrap)
            direction(FlexDirection.Row)
            alignContent(FlexAlignContent.Center)
            alignItems(FlexAlignItems.Center)
            gap(24.dp)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainHeader()
            Greeting()
        }
        Column(
            modifier = Modifier.fillMaxWidth().flex { grow(1f) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = {

                },
            ) {
                Text(text = stringResource(R.string.daily_challenge))
            }
            Button(
                onClick = {
                    navController.navigate(AppAdditionalDestinations.GALLERY.route)
                },
            ) {
                Text(text = stringResource(R.string.select_level))
            }
            Button(
                onClick = {
                    var generator = SudokuGenerator(numbers = 9)
                    // would else be empty
                    generator.creatRandomSudoku()
                    var sudoku = GameManager.SudokuGame(generator.getRandomSudoku())
                    sudokuViewModel.currentGame = sudoku;

                    navController.navigate(AppAdditionalDestinations.SUDOKU.route)
                },
            ) {
                Text(text = stringResource(R.string.random_level))
            }
            Button(
                onClick = {
                    val numbers = 9
                    var sudoku = GameManager.SudokuGame(SnapshotStateList(numbers*numbers){0})
                    sudokuViewModel.currentGame = sudoku;

                    navController.navigate(AppAdditionalDestinations.SUDOKU.route)
                },
            ) {
                Text(text = stringResource(R.string.create_Sudoku))
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

        val adSize = AdSize.LARGE_BANNER

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
                            val error: String = result.error.message
                            Log.e(null, "Banner ad failed to load: $error")
                        }
                    }
                }
            }
        }
}