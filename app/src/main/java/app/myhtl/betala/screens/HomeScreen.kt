package app.myhtl.betala.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.navigation.NavController
import app.myhtl.betala.AppAdditionalDestinations
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import app.myhtl.betala.R
import app.myhtl.betala.opensudoku.GameManager
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadResult
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen(navController: NavController){
    val context = LocalContext.current
    val activity = context as? Activity
    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { nonNullUri ->
            try {
                GameManager.parseSudokuFile(context, nonNullUri)
                navController.navigate(AppAdditionalDestinations.SUDOKU.route)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error parsing file", e)
            }
        }
    }
    var bannerAdState by remember { mutableStateOf<BannerAd?>(null) }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MainHeader(modifier = Modifier.padding(innerPadding))

            Greeting(
                modifier = Modifier.padding(innerPadding)
            )
            Row(Modifier
                .padding(horizontal = 50.dp)
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = {
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.daily_challenge))
                }
                Button(
                    onClick = {
                        getContent.launch(arrayOf("application/xml", "text/xml", "application/opensudoku"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.select_level))
                }
            }
            Button(
                modifier = Modifier.padding(horizontal = 50.dp, vertical = 10.dp),
                onClick = {
                    navController.navigate(AppAdditionalDestinations.SUDOKU.route)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text(text = stringResource(R.string.random_level))
            }
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
                            var error: String = result.error.message
                            Log.e(null, "Banner ad failed to load: $error")
                        }
                    }
                }
            }
        }
    }
}