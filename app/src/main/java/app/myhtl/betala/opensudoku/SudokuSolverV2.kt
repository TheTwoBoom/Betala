package app.myhtl.betala.opensudoku

import app.myhtl.betala.SudokuVarients.SudokuRule
import kotlin.math.sqrt

class SudokuSolverV2(
    private val inputData: ByteArray,
    private var numbers: Int,
    private var boxWidth: Int = sqrt(numbers.toDouble()).toInt(),
    private var boxHeight: Int = numbers / boxWidth,
    private val ruleSet: List<SudokuRule>,
) {
    private var data: ByteArray = ByteArray(numbers * numbers)

    fun setNewData(inputData: ByteArray) {
        data = inputData.copyOf()
        numbers = data.size
    }

    fun solveCell(i: Int): Boolean {
        var solvable = false
        val startNotes = IntArray(numbers * numbers) { (1 shl numbers) - 1 }
        setNotesInSameHouse(startNotes)
        TODO()
    }

    fun setNotesInSameHouse(notes: IntArray) {
        for (i in 0 until numbers) {
            for (rule in ruleSet) {
                rule.removeNotesWithRule(
                    notes = notes,
                    index = i,
                    value = TODO()
                )
            }
        }
    }

    fun setAllPossibleNotes(notes: IntArray) {
        //go through all cells
        for (i in 0 until numbers) {
            val value = data[i]
            //skip cells with numbers
            if (value != 0.toByte()) {
                notes[i] = 0//BooleanArray(numbers){false}
                for (rule in ruleSet) {
                    rule.removeNotesWithRule(notes = notes, index = i, value = data[i].toInt())

                }

                continue
            }
        }
        //doNakedSingles()
    }
}



