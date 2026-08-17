package app.myhtl.betala.opensudoku

import ads_mobile_sdk.nu
import android.util.Xml
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.math.sqrt

object GameManager {
    class SudokuGame(
        val name: String = "?",
        val data: SnapshotStateList<Int>,
        val size: Int = sqrt(data.size.toDouble()).toInt(),
        val boxWidth: Int = sqrt(sqrt(data.size.toDouble())).toInt(),
        val boxHeight: Int = sqrt(sqrt(data.size.toDouble())).toInt(),
        val noteData: SnapshotStateList<BooleanArray> = SnapshotStateList(data.size) {BooleanArray(sqrt(data.size.toDouble()).toInt())},
        var isFullyCorrect: Boolean = false,
        ) {
        val originalList = data.toList()


        fun changeValue(index: Int, value: Int) {
            if(originalList[index] == 0) {
                data[index] = value

                if (value != 0) clearNotes(index)
                updateAttributes()
            }
        }

        fun changeValues(indices: Set<Int>, value: Int){
            indices.forEach { index ->
                if(originalList[index] == 0) {
                    data[index] = value

                    if (value != 0) clearNotes(index)
                }
            }
            updateAttributes()
        }

        fun updateAttributes(){
            val isFullyFilled = data.count{ it != 0} == size*size
            if(isFullyFilled){
                isFullyCorrect = checkCorrect().all { it == 0 }
            } else isFullyCorrect = false
        }

        fun toggleNote(index: Int, value: Int) {
            if(value !in 1 .. size && data[index] == 0) return
            val noteArray = noteData[index]
            noteArray[value - 1] = !noteData[index][value - 1]
            noteData[index] = noteArray.copyOf()

        }

        fun toggleNotes(indices: Set<Int>, value: Int){
            if(value !in 1 .. size) return
            indices.forEach { index ->
                if (data[index] == 0) {
                    val noteArray = noteData[index]
                    noteArray[value - 1] = !noteData[index][value - 1]
                    noteData[index] = noteArray.copyOf()
                }            }
        }
        fun clearNotes(index: Int) {
            if(this.originalList[index] == 0) {
                noteData[index] = BooleanArray(size) { false }
            }
        }

        fun clearNotes(indices: Set<Int>){
            indices.forEach { index ->
                clearNotes(index)
            }
        }

        fun clearDataAt(index: Int) {
            if(originalList[index] == 0 && data[index] != 0){
                data[index] = 0
            }
        }

        fun clearDataAt(indices: Set<Int>){
            indices.forEach { index ->
                clearDataAt(index)
            }
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

            // 3. Check Subgrids (Viel robuster!)
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
                                val numbers = sqrt(gameChars.size.toDouble()).toInt()
                                val gameList = SnapshotStateList(gameChars.size) { 0 }
                                for (x in 0 until numbers) {
                                    for (y in 0 until numbers) {
                                        gameList[x * numbers + y] = gameChars[x * numbers + y] - '0'
                                    }
                                }
                                games.add(SudokuGame(data = gameList, name = name))
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