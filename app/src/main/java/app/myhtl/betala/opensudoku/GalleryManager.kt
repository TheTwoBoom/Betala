package app.myhtl.betala.opensudoku

import android.app.Activity
import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import kotlin.io.readText

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
        return listOf()
    }
    fun getStore(context: Context): List<GameManager.OpenSudoku> {
        return listOf()
    }
    fun copySudoku(context: Context, src: Uri) {
        TODO()
    }
}