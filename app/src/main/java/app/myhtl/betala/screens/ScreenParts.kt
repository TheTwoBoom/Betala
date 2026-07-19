
package app.myhtl.betala.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.navigation.NavController
import app.myhtl.betala.AppAdditionalDestinations
import app.myhtl.betala.ui.theme.BetalaTheme
import app.myhtl.betala.R
import app.myhtl.betala.SudokuViewModel
import app.myhtl.betala.opensudoku.GameManager
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Plane
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.ar.scene.PlaneRendererBase
import io.github.sceneview.math.Rotation
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberViewNodeManager
import io.github.sceneview.math.Scale
import io.github.sceneview.rememberMaterialLoader

@Composable
fun ARTapToPlace(sudokuGame: GameManager.SudokuGame) {
    var anchor by remember { mutableStateOf<Anchor?>(null) }
    val engine = rememberEngine()
    val materialLoader = rememberMaterialLoader(engine)
    val windowManager = rememberViewNodeManager()

    ARSceneView(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        cameraStream = rememberARCameraStream(materialLoader).apply {
            isDepthOcclusionEnabled = true
        },
        planeRenderer = true,
        planeRendererVersion = PlaneRendererBase.Version.V2,
        sessionConfiguration = { s, config ->
            config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
            config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            config.depthMode = Config.DepthMode.AUTOMATIC
        },
        onSessionUpdated = { s, frame ->
            if (anchor == null) {
                anchor = frame.getUpdatedPlanes()
                    .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                    ?.let { plane -> plane.createAnchorOrNull(plane.centerPose) }
            }
        }
    ) {
        anchor?.let { a ->
            AnchorNode(anchor = a) {
                ViewNode(
                    windowManager = windowManager,
                    scale = Scale(0.3f),
                    rotation = Rotation(270f, 0f, 0f)
                ) {
                    CreateSudoku(
                        Modifier,
                        rowCount = sudokuGame.size(),
                        cells = sudokuGame.data,
                        cellNotes = sudokuGame.noteData,
                        actions = SudokuActions(
                            setIndex = {},
                            onNumberSelected = {},
                            toggleNoteMode = {},
                            validate = { true },
                            isEditable = { false },
                            sameValue = { false },
                            isNoteMode = false,
                            erase = {},
                            isFinishedAndCorrect = true,
                            // TODO: make the number variable
                            getNumbers = 9,
                            isPrinting = true
                        ),
                        selectedCell = -1,
                    )
                }
            }
        }
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    text: String,
    returnDest: String = "home",
    navController: NavController,
    popup: MutableState<Boolean> = remember { mutableStateOf(false) },
    leftButton: @Composable (RowScope.() -> Unit) = {
        IconButton(
            onClick = { navController.navigate(returnDest) },
            shapes = IconButtonDefaults.shapes(),
        ) {
            Icon(
                painter = painterResource(R.drawable.close),
                contentDescription = "Exit"
            )
        }
    },
    rightButton: @Composable (RowScope.() -> Unit)? = null,
    textComposable: @Composable (RowScope.() -> Unit) = {
        Text(
            text = text,
            fontSize = 32.sp,
            fontWeight = FontWeight(350)
        )
    },
) {
    var expanded by remember { mutableStateOf(false) }

    if (popup.value) {
        AboutDialog { popup.value = false }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leftButton()
        textComposable()
        if (rightButton != null) {
            rightButton()
        } else {
            Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                IconButton(
                    onClick = { expanded = !expanded },
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(painterResource(R.drawable.more_vert), "Dropdown")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    content = {
                        DropdownMenuItem(
                            text = { Text("About Betala") },
                            onClick = {
                                expanded = false
                                popup.value = true
                            }
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuCarousel(
    text: String,
    items: SnapshotStateList<GameManager.OpenSudoku>,
    navController: NavController,
    sudokuViewModel: SudokuViewModel,
    isLoading: Boolean = false,
) {
    val placeholderCount = 3
    val carouselItemCount = if (isLoading) placeholderCount else items.size

    Column(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight(400)
        )
        HorizontalCenteredHeroCarousel(
            state = rememberCarouselState { carouselItemCount },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { i ->
            if (isLoading || i !in items.indices) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.15f)
                        .maskClip(MaterialTheme.shapes.extraLarge)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val item = items[i]
                val colorStops = arrayOf(
                    0.0f to Color(139, 222, 218),
                    0.25f to Color(67, 173, 208),
                    0.5f to Color(153, 142, 224),
                    0.75f to Color(225, 125, 194),
                    1f to Color(239, 147, 147)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.15f)
                        .maskClip(MaterialTheme.shapes.extraLarge)
                        .background(
                            Brush.horizontalGradient(colorStops = colorStops),
                            alpha = 0.75f
                        )
                        .clickable(true) {
                            if (item.games.isNotEmpty()) {
                                sudokuViewModel.currentGame = item.games[0]
                                navController.navigate(AppAdditionalDestinations.SUDOKU.route)
                            }
                        }
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(0.75f),
                            bitmap = item.games[0].preview,
                            contentDescription = "Sudoku Preview",
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = item.name + " by " + item.author,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
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
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun AboutDialog(
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "BETALA",
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 48.sp,
                    fontFamily = FontFamily(Font(R.font.majormonodisplay)),
                )
                Text(
                    text = "Betala v0.0.1 \n" +
                            "Copyright © 2026 by the Betala Developers",
                    modifier = Modifier.padding(8.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BetalaTheme {
    }
}