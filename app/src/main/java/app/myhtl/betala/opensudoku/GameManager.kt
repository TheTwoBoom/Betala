package app.myhtl.betala.opensudoku

import android.content.Context
import android.net.Uri
import android.util.Xml
import app.myhtl.betala.utils.readTextFromUri
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.io.InputStream

object GameManager {
    data class SudokuGame(val data: MutableList<Int>) {
        private fun index(x: Int, y: Int): Int = y * 9 + x

        fun changeValue(x: Int, y: Int, value: Int) {
            if (x in 0..8 && y in 0..8 && value in 1..9) {
                data[index(x, y)] = value
            }
        }
        fun checkCorrect(): Boolean {
            // Check rows
            for (i in 0..8) {
                val seen = BooleanArray(9)
                for (j in 0..8) {
                    val value = data[index(j, i)]
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
                    val value = data[index(j, i)]
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
                            val value = data[index(boxCol * 3 + j, boxRow * 3 + i)]
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
        fun getNumSet(): List<Int> {
            return listOf(1,2,3,4,5,6,7,8,9)
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

    fun parseSudokuFile(context: Context, uri: Uri?): OpenSudoku? {
        val xmlString: String
        try {
            xmlString = readTextFromUri(context, uri!!)
        } catch (_: Exception) {
            return null
        }

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
                                val gameList = MutableList(81) { 0 }
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