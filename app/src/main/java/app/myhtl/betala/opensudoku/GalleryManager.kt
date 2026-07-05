package app.myhtl.betala.opensudoku

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.print.PrintHelper
import app.myhtl.betala.utils.readTextFromUri
import com.google.crypto.tink.subtle.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import app.myhtl.betala.screens.CreateSudoku
import app.myhtl.betala.screens.SudokuActions
import app.myhtl.betala.utils.captureComposable
import app.myhtl.betala.utils.useVirtualDisplay

object GalleryManager {
    var allSudokus: SnapshotStateList<GameManager.OpenSudoku> = SnapshotStateList()
    suspend fun fetchAllSudokus(context: Context) = coroutineScope {
        val predefinedFiles = context.assets.list("")?.filter { it.endsWith(".opensudoku") }.orEmpty()
        val userFiles = context.filesDir.list()?.filter { it.endsWith(".opensudoku") }.orEmpty()

        val sudokuJobs = buildList {
            predefinedFiles.forEach { fileName ->
                add(async { loadPredefinedSudoku(context, fileName) })
            }
            userFiles.forEach { fileName ->
                add(async { loadUserSudoku(context, fileName) })
            }
        }
        sudokuJobs.awaitAll()
    }
    fun getAllSudokus(context: Context): List<GameManager.OpenSudoku> {
        if (allSudokus.isEmpty()) {
            runBlocking { fetchAllSudokus(context) }
        }
        return allSudokus
    }
    fun getFavoriteSudokus(context: Context): List<GameManager.OpenSudoku> {
        val activity = context as? Activity
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val favorites = sharedPref?.getStringSet("favoriteSudokus", HashSet<String>())?.toMutableSet()
        favorites?.add("")
        val gameList: List<GameManager.OpenSudoku> = allSudokus.filter { favorites?.contains(it.name) == true }
        return gameList
    }
    suspend fun loadPredefinedSudoku(context: Context, fileName: String) {
        return withContext(Dispatchers.IO) {
            val sudokuString = context.assets.open(fileName).bufferedReader().use(BufferedReader::readText)
            GameManager.parseSudokuFile(sudokuString)?.let { allSudokus.add(it) }
        }
    }

    suspend fun loadUserSudoku(context: Context, fileName: String) {
        return withContext(Dispatchers.IO) {
            val sudokuString = readTextFromUri(context, context.filesDir.resolve(fileName).toUri())
            GameManager.parseSudokuFile(sudokuString)?.let { allSudokus.add(it) }
        }
    }

    @Suppress("unused")
    fun getStore(): List<GameManager.OpenSudoku> {
        return listOf()
    }

    fun importSudoku(context: Context, src: Uri) {
        val sudokuName = Random.randInt(1000).toString() + ".opensudoku"
        val outputStream = context.filesDir
            .resolve(sudokuName)
            .outputStream()
        context.contentResolver.openInputStream(src)?.use { inputStream ->
            var readByte = inputStream.read()
            while (readByte != -1) {
                outputStream.write(readByte)
                readByte = inputStream.read()
            }
        }
        outputStream.close()
        runBlocking {loadUserSudoku(context, sudokuName)}
    }

    suspend fun createBitmapFromSudoku(context: Context, sudokuGame: GameManager.SudokuGame, size: DpSize = DpSize(800.dp, 800.dp)): ImageBitmap {
        // Use the persistent virtual display manager instead of creating a new one each time
        val bitmap = useVirtualDisplay(context) { display ->
            captureComposable(
                context = context,
                size = size,
                display = display
            ) {
                LaunchedEffect(Unit) {
                    capture()
                }
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
                        //Attention: make the number variable
                        getNumbers = 9,
                        isPrinting = true
                    ),
                    selectedCell = -1,
                )
            }
        }
        return bitmap
    }

    fun printBitmap(activity: Activity, bitmap: ImageBitmap) {
        activity.also { context ->
            PrintHelper(context).apply {
                scaleMode = PrintHelper.SCALE_MODE_FIT
            }.also { printHelper ->
                printHelper.printBitmap("Betala Sudoku", bitmap.asAndroidBitmap())
            }
        }
    }
}