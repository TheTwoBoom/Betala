package app.myhtl.betala.opensudoku

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import java.io.BufferedReader
import kotlin.io.readText
import app.myhtl.betala.utils.readTextFromUri
import com.google.crypto.tink.subtle.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object GalleryManager {
    var allSudokus: MutableList<GameManager.OpenSudoku> = mutableListOf()
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
    fun getAllSudokus(context: Context): MutableList<GameManager.OpenSudoku> {
        if (allSudokus.isEmpty()) {
            runBlocking { fetchAllSudokus(context) }
        }
        return allSudokus
    }
    fun getFavoriteSudokus(context: Context): MutableList<GameManager.OpenSudoku> {
        val activity = context as? Activity
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val favorites = sharedPref?.getStringSet("favoriteSudokus", HashSet<String>())?.toMutableSet()
        favorites?.add("")
        val gameList: MutableList<GameManager.OpenSudoku> = allSudokus.filter { favorites?.contains(it.name) == true }.toMutableList()
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
    }
}