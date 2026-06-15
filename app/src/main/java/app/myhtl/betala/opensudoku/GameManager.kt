package app.myhtl.betala.opensudoku

import android.content.Context
import android.net.Uri
import android.util.Xml
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.myhtl.betala.utils.readTextFromUri
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.math.sqrt

object GameManager {
    class SudokuGame(
        val data: SnapshotStateList<Int>,
        val changed: MutableList<Int> = MutableList(data.size) {0},
        val noteData: SnapshotStateList<BooleanArray> = SnapshotStateList(data.size) {BooleanArray(9)},
        var isFullyFilled: Boolean = false,
        var filledCells: Int = 0,
        var isFullyCorrect: Boolean = false
    ) {
        public fun size(): Int = sqrt(data.size.toDouble()).toInt()
        public fun index(x: Int, y: Int): Int = y * size() + x

        fun changeValue(index: Int, value: Int) {
            if(this.getOriginal()[index] == 0) {
                changed[index] = value
                data[index] = value
                if (value != 0) {
                    clearNotes(index)
                }
            }
            updateAttributes()
        }

        fun updateAttributes(){
            filledCells = 0
            data.forEach { value ->
                if(value != 0) filledCells++
            }
            if(filledCells == getNumSet().size*getNumSet().size) isFullyFilled = true else isFullyFilled = false
            if(isFullyFilled){
                checkCorrect().forEach { value ->
                    if(value != 0){
                        isFullyCorrect = false
                        return
                    }
                }
                isFullyCorrect = true
            }
        }

        fun toggleNote(index: Int, value: Int) {
            if (data[index] == 0) {
                var noteArray: BooleanArray = noteData[index]
                noteArray[value - 1] = !noteData[index][value - 1]
                noteData[index] = noteArray.copyOf()
            }
        }
        fun clearNotes(index: Int) {
            if(this.getOriginal()[index] == 0) {
                noteData[index] = BooleanArray(9) { false }
            }
        }
        fun clearDataAt(index: Int) {
            if(this.getOriginal()[index] == 0){
                updateAttributes()
                data[index] = 0
            }
        }

        fun checkCorrect(): List<Int> {
            val falseList: MutableList<Int> = MutableList(data.size) {0}
            // Check rows
            for (i in 0..8) {
                val seen = BooleanArray(9)
                for (j in 0..8) {
                    val value = data[index(j, i)]
                    if (value != 0) {
                        if (seen[value - 1]) falseList[index(j, i)] = value
                        seen[value - 1] = true
                    }
                }
            }
            // Check columns
            for (j in 0..8) {
                val seen = BooleanArray(9)
                for (i in 0..8) {
                    val value = data[index(j, i)]
                    if (value != 0) {
                        if (seen[value - 1]) falseList[index(j, i)] = value
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
                            val value = data[index(boxCol * 3 + j, boxRow * 3 + i)]
                            if (value != 0) {
                                if (seen[value - 1]) falseList[index(j, i)] = value
                                seen[value - 1] = true
                            }
                        }
                    }
                }
            }
            return falseList
        }
        fun getOriginal(): List<Int> {
            val originalList: MutableList<Int> = data.toMutableList()
            for (i in 0 until data.size) {
                if (changed[i] != 0) {
                    originalList[i] = 0
                }
            }
            return originalList
        }
        fun getNumSet(): List<Int> {
            return (1..size()).toList()
        }
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SudokuGame

            if (data != other.data) return false

            return true
        }
        override fun hashCode(): Int {
            return data.hashCode()
        }
    }

    data class OpenSudoku(
        val name: String,
        val author: String,
        val level: String,
        val created: String,
        val source: String,
        val sourceURL: String,
        val games: List<SudokuGame>
    )

    fun parseSudokuFile(xmlString: String): OpenSudoku? {
        val parser: XmlPullParser = Xml.newPullParser()
        val inputStream: InputStream = ByteArrayInputStream(xmlString.toByteArray())

        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)
        parser.nextTag()
        var name = ""
        var author = ""
        var level = ""
        var created = ""
        var source = ""
        var sourceURL = ""
        val games = mutableListOf<SudokuGame>()

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            when (parser.eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "name" -> name = parser.nextText()
                        "author" -> author = parser.nextText()
                        "level" -> level = parser.nextText()
                        "created" -> created = parser.nextText()
                        "source" -> source = parser.nextText()
                        "sourceURL" -> sourceURL = parser.nextText()
                        "game" -> {
                            val data = parser.getAttributeValue(null, "data")
                            try {
                                val gameChars = data.toCharArray()
                                val gameList = SnapshotStateList(81) { 0 }
                                for (x in 0..8) {
                                    for (y in 0..8) {
                                        gameList[x * 9 + y] = gameChars[x * 9 + y] - '0'
                                    }
                                }
                                games.add(SudokuGame(gameList))
                            } catch (_: Exception) {
                                return null
                            }
                        }
                    }
                }
            }
        }
        return OpenSudoku(name, author, level, created, source, sourceURL, games)
    }
}