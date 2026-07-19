package app.myhtl.betala.opensudoku

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeFile
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.print.PrintHelper
import app.myhtl.betala.screens.CreateSudoku
import app.myhtl.betala.screens.SudokuActions
import app.myhtl.betala.utils.FilterOption
import app.myhtl.betala.utils.captureComposable
import app.myhtl.betala.utils.readTextFromUri
import app.myhtl.betala.utils.useVirtualDisplay
import com.google.crypto.tink.subtle.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.BufferedReader
import java.io.FileOutputStream
import kotlin.math.sqrt


object GalleryManager {
    var allSudokus: SnapshotStateList<GameManager.OpenSudoku> = SnapshotStateList()

    var isLoading by mutableStateOf(true)
        private set

    suspend fun fetchAllSudokus(context: Context) = coroutineScope {
        isLoading = true

        try {
            val predefinedFiles = context.assets.list("")
                ?.filter { it.endsWith(".opensudoku") }
                .orEmpty()
            val userFiles = context.filesDir.list()
                ?.filter { it.endsWith(".opensudoku") }
                .orEmpty()

            val sudokuJobs = buildList {
                predefinedFiles.forEach { fileName ->
                    add(async { loadPredefinedSudoku(context, fileName) })
                }
                userFiles.forEach { fileName ->
                    add(async { loadUserSudoku(context, fileName) })
                }
            }

            sudokuJobs.awaitAll()
        } catch (exception: Exception) {
            Log.e("GalleryManager", "Sudokus wurden nicht erfolgreich geladen", exception)
        }
    }

    suspend fun generatePreviews(context: Context) {
        for (i in allSudokus.indices) {
            val openSudoku = allSudokus[i]
            for (game in openSudoku.games) {
                withContext(Dispatchers.Main) {
                    game.preview = createBitmapFromSudoku(context, game.data.toList())
                }
                yield()
            }
            // SnapshotStateList-Änderung triggern → UI aktualisiert sich
            allSudokus[i] = openSudoku
        }
        isLoading = false
    }

    fun getAllSudokus(context: Context): List<GameManager.OpenSudoku> {
        return allSudokus
    }

    fun getFilteredSudokus(
        context: Context,
        filters: List<FilterOption>
    ): List<GameManager.OpenSudoku> {
        var list: List<GameManager.OpenSudoku> = getAllSudokus(context)
        val fFilter = filters.firstOrNull { it.id == "favorite"}
        val lFilter = filters.firstOrNull { it.id == "level" }
        val aFilter = filters.firstOrNull { it.id == "author" }

        if (fFilter != null) { list = filterFavorites(fFilter, list, context) }
        if (lFilter != null) { list = filterDifficulty(lFilter, list) }
        if (aFilter != null) { list = filterAuthor(aFilter, list) }
        // if () { list = filter }
        // TODO: implement other filters

        return list
    }

    fun filterFavorites(filter: FilterOption, list: List<GameManager.OpenSudoku>, context: Context): List<GameManager.OpenSudoku> {
        val sharedPref = (context as? Activity)?.getPreferences(Context.MODE_PRIVATE)
        val favorites = sharedPref?.getStringSet("favoriteSudokus", HashSet<String>())
            ?.toMutableSet()
        return if (filter.options.first().isSelected) {
            list.filter { favorites?.contains(it.name)!! }
        } else list
    }
    fun filterDifficulty(filter: FilterOption, list: List<GameManager.OpenSudoku>): List<GameManager.OpenSudoku> {
        val selectedLevels = filter
            .options
            .filter { it.isSelected }
            .map { it.id }
            .toSet()

        return if (selectedLevels.isNotEmpty()) {
            list.filter { sudoku -> sudoku.level in selectedLevels }
        } else list
    }
    fun filterAuthor(filter: FilterOption, list: List<GameManager.OpenSudoku>): List<GameManager.OpenSudoku> {
        TODO()
    }
    suspend fun loadPredefinedSudoku(context: Context, fileName: String) {
        val sudokuString = withContext(Dispatchers.IO) {
            context.assets
                .open(fileName)
                .bufferedReader()
                .use(BufferedReader::readText)
        }

        val sudoku = GameManager.parseSudokuFile(sudokuString)

        withContext(Dispatchers.Main.immediate) {
            sudoku?.let(allSudokus::add)
        }
    }

    suspend fun loadUserSudoku(context: Context, fileName: String) {
        val sudokuString = withContext(Dispatchers.IO) {
            readTextFromUri(
                context,
                context.filesDir.resolve(fileName).toUri()
            )
        }

        val sudoku = GameManager.parseSudokuFile(sudokuString)

        withContext(Dispatchers.Main.immediate) {
            sudoku?.let(allSudokus::add)
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
        runBlocking { loadUserSudoku(context, sudokuName) }
    }

    suspend fun createBitmapFromSudoku(context: Context, sudokuData: List<Int>, size: DpSize = DpSize(800.dp, 800.dp)): ImageBitmap {
        var bitmap: ImageBitmap
        var sudokuString = ""
        sudokuData.forEach { sudokuString += it }
        if (context.cacheDir?.list()?.contains("$sudokuString.webp") == true) {
            println("Image was cached")
            bitmap = decodeFile(context.cacheDir.path + "/" + "$sudokuString.webp").asImageBitmap()
        //bitmap = ImageBitmap(800, 800)
        } else {
            bitmap = useVirtualDisplay(context) { display ->
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
                        rowCount = sqrt(sudokuData.size.toDouble()).toInt(),
                        cells = sudokuData,
                        cellNotes = List(81) { BooleanArray(9) { false } },
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
            val path = context.cacheDir.path + "/" + "$sudokuString.webp"
            withContext(Dispatchers.IO) {
                val fileOutputStream = FileOutputStream(path)
                bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 50, fileOutputStream)
                fileOutputStream.close()
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