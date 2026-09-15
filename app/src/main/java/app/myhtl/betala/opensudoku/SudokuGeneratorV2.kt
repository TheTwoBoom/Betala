package app.myhtl.betala.opensudoku

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.myhtl.betala.SudokuVarients.SudokuRule
import kotlin.math.sqrt
import kotlin.random.Random

class SudokuGeneratorV2(
    private val numbers: Int,
    private var boxWidth: Int = sqrt(numbers.toDouble()).toInt(),
    private var boxHeight: Int = numbers / boxWidth,
    private val ruleSet: List<SudokuRule>,
    private val difficulty: Difficulty
    ) {
    private val data: ByteArray = ByteArray(numbers * numbers)


    fun getNewSudoku(): SnapshotStateList<Int> {
        val countTime = System.currentTimeMillis()
        data.fill(0)
        val startNotes = IntArray(numbers * numbers) { (1 shl numbers) - 1 }
        createSudokuRecursively(startNotes)

        Log.d("time", "" + (System.currentTimeMillis() - countTime))
        return mutableStateListOf<Int>().apply {
            addAll(data.map { it.toInt() })
        }
    }

    fun testSudokuGeneratorTime(amount: Int) {
        var totalNanoseconds = 0.0
        var bestTimeNs = -1.0
        var worstTimeNs = -1.0

        // Warm-up (optional aber empfohlen für JIT-Optimierung)
        repeat(5) {
            data.fill(0)
            createSudokuRecursively(IntArray(numbers * numbers) { (1 shl numbers) - 1 })
        }

        for (i in 0 until amount) {
            val countTime = System.nanoTime()
            data.fill(0)
            val startNotes = IntArray(numbers * numbers) { (1 shl numbers) - 1 }
            createSudokuRecursively(startNotes)
            val finalTimeNs = (System.nanoTime() - countTime).toDouble()

            // Formatierung:
            // %3d -> Index i auf 3 Stellen (0-999)
            // %10.0f -> Zeit auf 10 Stellen ohne Nachkommastellen
            // %8.2f -> Zeit in µs auf 8 Stellen mit 2 Nachkommastellen
            val logMessage = String.format(
                "Sudoku %3d: %10.0f ns | %8.2f µs",
                i, finalTimeNs, finalTimeNs / 1000.0
            )
            Log.d("SudokuBench", logMessage)

            if (finalTimeNs < bestTimeNs || bestTimeNs == -1.0) bestTimeNs = finalTimeNs
            if (finalTimeNs > worstTimeNs || worstTimeNs == -1.0) worstTimeNs = finalTimeNs

            totalNanoseconds += finalTimeNs
        }

        val averageTimeNs = totalNanoseconds / amount

        Log.d("SudokuBench", "--------------------------------------------------")
        Log.d("SudokuBench", "Size: $numbers*$numbers")
        Log.d(
            "SudokuBench",
            String.format(
                "Fastest: %10.2f µs | %7.4f ms",
                bestTimeNs / 1000.0,
                bestTimeNs / 1_000_000.0
            )
        )
        Log.d(
            "SudokuBench",
            String.format(
                "Slowest: %10.2f µs | %7.4f ms",
                worstTimeNs / 1000.0,
                worstTimeNs / 1_000_000.0
            )
        )
        Log.d(
            "SudokuBench",
            String.format(
                "Average: %10.2f µs | %7.4f ms",
                averageTimeNs / 1000.0,
                averageTimeNs / 1_000_000.0
            )
        )
        Log.d("SudokuBench", String.format("Total:   %10.2f ms", totalNanoseconds / 1_000_000.0))
        Log.d("SudokuBench", "--------------------------------------------------")
    }

    fun createSudokuRecursively(notes: IntArray): Boolean {
        val nextIndex = findBestCell(notes)
        if (nextIndex == -1) return true
        if (nextIndex == -2) return false

        var notesAtCell = notes[nextIndex]

        while (notesAtCell != 0) {
            //maybe unnecessary
            val bit = notesAtCell.takeLowestOneBit()

            val value = Integer.numberOfTrailingZeros(bit) + 1

            //to restore the notes for backtracking
            //TODO() clone only once, not in while, make remember
            val notesCopy = notes.clone()

            data[nextIndex] = value.toByte()

            for (rule in ruleSet) {
                rule.removeNotesWithRule(
                    notes = notesCopy,
                    index = nextIndex,
                    value = value
                )
            }

            if (createSudokuRecursively(notesCopy)) return true

            data[nextIndex] = 0
            notesAtCell = notesAtCell xor bit
        }

        return false
    }

    fun findBestCell(notes: IntArray): Int {
        var bestCell = -1
        var minNotes = numbers + 1
        var countTies = 0
        for (i in notes.indices) {
            if (data[i] == 0.toByte()) {
                val countNotes = Integer.bitCount(notes[i])

                if (countNotes == 0) return -2
                if (countNotes == 1) return i

                if (countNotes < minNotes) {
                    minNotes = countNotes
                    bestCell = i
                    countTies = 1
                } else if (countNotes == minNotes) {
                    countTies++

                    if (Random.nextInt(countTies) == 0) {
                        bestCell = i
                    }
                }
            }
        }
        return bestCell
    }


    fun removeNumbers(amount: Int) {
        /*val solver = SudokuSolver(
            solveOnInit = false,
            inputData = data,
            boxWidth = boxWidth,
            boxHeight = boxHeight,
            ruleSet = ruleSet
        )*/

        for (i in 0 until amount) {
            val cell = findBestCellToRemove()

        }
    }

    fun findBestCellToRemove(): Int{
        var bestCell = -1
        for(i in 0 until data.size){
            if(data[i] != 0.toByte()){
                if (Random.nextInt(i) == 0) {
                    bestCell = i
                }
            }
        }
        return bestCell
    }
}