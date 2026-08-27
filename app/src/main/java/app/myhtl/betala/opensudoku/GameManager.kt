package app.myhtl.betala.opensudoku

import android.util.Log
import android.util.Xml
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.math.sqrt


object GameManager {
    class SudokuGame(
        val data: SnapshotStateList<Int>,
        var preview: ImageBitmap = ImageBitmap(
            width = 1,
            height = 1
        ),
        val size: Int = sqrt(data.size.toDouble()).toInt(),
        val boxWidth: Int = sqrt(sqrt(data.size.toDouble())).toInt(),
        val boxHeight: Int = sqrt(sqrt(data.size.toDouble())).toInt(),
        val noteData: SnapshotStateList<BooleanArray> = SnapshotStateList(data.size) {BooleanArray(sqrt(data.size.toDouble()).toInt())},
        var isFullyCorrect: Boolean = false,
        //TODO() make a subclass, which contains name, difficulty, variant, instead of in the Viewmodel. Then change it in the SudokuScreen
        val name: String = "?",
        ) {
        val originalList = data.toList()


//        fun changeValue(index: Int, value: Int) {
//            if(originalList[index] != 0) return
//            data[index] = value
//            if (value != 0) clearNotes(index)
//            updateAttributes()
//        }

        fun changeValues(indices: Set<Int>, value: Int): Boolean{
            var counter = 0
            indices.forEach { index ->
                if(originalList[index] == 0 && data[index] != value) {
                    data[index] = value

                    if (value != 0) clearNotes(index)
                } else counter++
            }
            if(counter == indices.size) return false

            updateAttributes()
            return true
        }

        fun updateAttributes(){
            val isFullyFilled = data.count{ it != 0} == size*size
            isFullyCorrect = if(isFullyFilled){
                checkCorrect().all { it == 0 }
            } else false
        }

//        fun toggleNote(index: Int, value: Int) {
//            if(value !in 1 .. size && data[index] == 0) return
//            val noteArray = noteData[index]
//            noteArray[value - 1] = !noteData[index][value - 1]
//            noteData[index] = noteArray.copyOf()
//
//        }

        fun toggleNotes(indices: Set<Int>, value: Int): Boolean{
            if(value !in 1 .. size) return false
            var counter = 0
            indices.forEach { index ->
                if (data[index] == 0) {
                    val noteArray = noteData[index]
                    noteArray[value - 1] = !noteData[index][value - 1]
                    noteData[index] = noteArray.copyOf()
                }   else{
                    counter++
                }
            }
            return counter != indices.size
        }
        fun clearNotes(index: Int) {
            if(this.originalList[index] == 0) {
                noteData[index] = BooleanArray(size) { false }
            }
        }

        fun clearNotes(indices: Set<Int>): Boolean{
            var counter = 0
            indices.forEach { index ->
                if(this.originalList[index] == 0 && noteData[index].any{ it }) {
                    noteData[index] = BooleanArray(size) { false }
                } else{
                    counter++
                }
            }

            return counter != indices.size
        }

//        fun clearDataAt(index: Int) {
//            if(originalList[index] == 0 && data[index] != 0){
//                data[index] = 0
//            }
//        }

        fun clearDataAt(indices: Set<Int>): Boolean{
            var counter = 0
            indices.forEach { index ->
                if(originalList[index] == 0 && data[index] != 0){
                    data[index] = 0
                } else{
                    counter++
                }
            }
            return counter != indices.size
        }

        fun checkCorrect(): List<Int> {
            val falseList: MutableList<Int> = MutableList(data.size) { 0 }
            val s = size

            fun markDuplicates(indices: List<Int>) {
                val seenPositions = mutableMapOf<Int, MutableList<Int>>()
                for (pos in indices) {
                    val value = data[pos]
                    if (value != 0) {
                        seenPositions.getOrPut(value) { mutableListOf() }.add(pos)
                    }
                }
                for ((value, positions) in seenPositions) {
                    if (positions.size > 1) {
                        for (pos in positions) falseList[pos] = value
                    }
                }
            }

            // 1. Check Rows
            for (row in 0 until s) {
                val rowIndices = (0 until s).map { col -> row*size + col }
                markDuplicates(rowIndices)
            }

            // 2. Check Columns
            for (col in 0 until s) {
                val colIndices = (0 until s).map { row -> row*size + col }
                markDuplicates(colIndices)
            }

            // 3. Check SubGrids (Viel robuster!)
            val numBlocksWide = s / boxWidth
            val numBlocksHigh = s / boxHeight

            for (by in 0 until numBlocksHigh) {
                for (bx in 0 until numBlocksWide) {
                    val boxIndices = mutableListOf<Int>()
                    for (y in 0 until boxHeight) {
                        for (x in 0 until boxWidth) {
                            val globalX = bx * boxWidth + x
                            val globalY = by * boxHeight + y
                            boxIndices.add(globalY*size + globalX)
                        }
                    }
                    markDuplicates(boxIndices)
                }
            }
            return falseList
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SudokuGame

            return data == other.data
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

    suspend fun parseSudokuFile(xmlString: String): OpenSudoku? {
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
                            val encodedGame =
                                parser.getAttributeValue(null, "data")

                            try {
                                requireNotNull(encodedGame) {
                                    "Das Attribut 'data' fehlt."
                                }
                                require(encodedGame.length == 81) {
                                    "Erwartet wurden 81 Zeichen, erhalten: ${encodedGame.length}"
                                }
                                require(encodedGame.all { it in '0'..'9' }) {
                                    "Das Sudoku enthält ungültige Zeichen."
                                }

                                val parsedValues = encodedGame.map { character ->
                                    character.digitToInt()
                                }
                                val game = withContext(Dispatchers.Main.immediate) {
                                    val gameList = mutableStateListOf<Int>().apply {
                                        addAll(parsedValues)
                                    }
                                    SudokuGame(gameList, ImageBitmap(1, 1), name = name)
                                }

                                games.add(game)
                                Log.d("GameManager", "Sudoku added")
                            } catch (exception: Exception) {
                                Log.e("GameManager", "Sudoku konnte nicht geladen werden", exception)
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