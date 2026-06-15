package app.myhtl.betala.opensudoku

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import java.io.BufferedReader
import kotlin.io.readText
import app.myhtl.betala.utils.readTextFromUri
import com.google.crypto.tink.subtle.Random

object GalleryManager {
    fun getFavoriteSudokus(context: Context): MutableList<GameManager.OpenSudoku> {
        val activity = context as? Activity
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val favorites = sharedPref?.getStringSet("favoriteSudokus", HashSet<String>())
        favorites?.add("medium.opensudoku")
        val gameList: MutableList<GameManager.OpenSudoku> = mutableListOf()
        if (favorites != null) {
            for (favorite in favorites) {
                val sudokuString = context.assets.open(favorite).bufferedReader().use(BufferedReader::readText)
                GameManager.parseSudokuFile(sudokuString)?.let { gameList.add(it) }
            }
        }
        return gameList
    }
    fun getAllSudokus(context: Context): List<GameManager.OpenSudoku> {
        val gameList: MutableList<GameManager.OpenSudoku> = mutableListOf()
        for (fileName in context.assets.list("")!!) {
            if (fileName.endsWith(".opensudoku")) {
                val sudokuString = context.assets.open(fileName).bufferedReader().use(BufferedReader::readText)
                GameManager.parseSudokuFile(sudokuString)?.let { gameList.add(it) }
            }
        }
        for (fileName in context.filesDir.list()!!) {
            if (fileName.endsWith(".opensudoku")) {
                val sudokuString = readTextFromUri(context, context.filesDir.resolve(fileName).toUri())
                GameManager.parseSudokuFile(sudokuString)?.let { gameList.add(it) }
            }
        }
        return gameList
    }
    fun getStore(context: Context): List<GameManager.OpenSudoku> {
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