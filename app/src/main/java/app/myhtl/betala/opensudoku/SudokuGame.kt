package app.myhtl.betala.opensudoku

import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult

object SudokuGame {
    data class Game(val board: Array<IntArray>) {
        fun changeValue(x: Int, y: Int, value: Int) {
            if (x in 0..8 && y in 0..8 && value in 1..9) {
                board[y][x] = value
            }
        }

        fun checkCorrect(): Boolean {
            // Check rows
            for (i in 0..8) {
                val seen = BooleanArray(9)
                for (j in 0..8) {
                    val value = board[i][j]
                    if (value != 0) {
                        if (seen[value - 1]) return false
                        seen[value - 1] = true
                    }
                }
            }
            // Check columns
            for (j in 0..8) {
                val seen = BooleanArray(9)
                for (i in 0..8) {
                    val value = board[i][j]
                    if (value != 0) {
                        if (seen[value - 1]) return false
                        seen[value - 1] = true
                    }
                }
            }
            // Check 3x3 subgrids
            for (boxRow in 0..2) {
                for (boxCol in 0..2) {
                    val seen = BooleanArray(9)
                    for (i in 0..2) {
                        for (j in 0..2) {
                            val value = board[boxRow * 3 + i][boxCol * 3 + j]
                            if (value != 0) {
                                if (seen[value - 1]) return false
                                seen[value - 1] = true
                            }
                        }
                    }
                }
            }
            return true
        }
    }
    fun getFileFromUser(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/xml"
        }
        startActivityForResult(activity, intent, 2, null)
    }
    fun parseSudokuFile(fileContent: String) {}
}