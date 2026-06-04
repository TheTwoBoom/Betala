package app.myhtl.betala.opensudoku

import android.content.Context
import android.net.Uri
import android.util.Xml
import app.myhtl.betala.utils.readTextFromUri
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.io.InputStream

object GameManager {
    data class SudokuGame(val data: Array<IntArray>) {
        fun changeValue(x: Int, y: Int, value: Int) {
            if (x in 0..8 && y in 0..8 && value in 1..9) {
                data[y][x] = value
            }
        }
        fun checkCorrect(): Boolean {
            // Check rows
            for (i in 0..8) {
                val seen = BooleanArray(9)
                for (j in 0..8) {
                    val value = data[i][j]
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
                    val value = data[i][j]
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
                            val value = data[boxRow * 3 + i][boxCol * 3 + j]
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
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SudokuGame

            if (!data.contentDeepEquals(other.data)) return false

            return true
        }
        override fun hashCode(): Int {
            return data.contentDeepHashCode()
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
                                val gameArray: Array<IntArray> = Array(9, init = {return@Array IntArray(9)})
                                for (x in 0..8) {
                                    for (y in 0..8) {
                                        gameArray[x][y] = gameChars[x * 9 + y] - '0'
                                    }
                                }
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