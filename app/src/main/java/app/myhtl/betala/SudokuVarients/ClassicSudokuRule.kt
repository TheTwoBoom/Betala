package app.myhtl.betala.SudokuVarients

import app.myhtl.betala.opensudoku.clearNote
import kotlin.math.sqrt

class ClassicSudokuRule(
    private var numbers: Int,
    private val boxWidth: Int = sqrt(numbers.toDouble()).toInt(),
    private val boxHeight: Int = numbers/boxWidth,

) : SudokuRule {
//    override fun removeNotesWithRule(notes: Array<Array<BooleanArray>>, index: Int, value: Int
//    ) {
//        val cellRow = index /numbers
//        val cellColumn = index %numbers
//
//        val boxRowStart = (cellRow/boxHeight)*boxHeight
//        val boxColStart = (cellColumn/boxWidth)*boxWidth
//
//        // in this loop, the notes are set based after the random number that has been added
//        for(i in 0 until numbers){
//            notes[i][cellColumn][value -1] = false
//            notes[cellRow][i][value -1] = false
//            notes[boxRowStart+i/boxWidth][boxColStart+i%boxWidth][value -1] = false
//        }
//
//
//    }

    override fun removeNotesWithRule(notes: IntArray, index: Int, value: Int
    ) {
        val cellRow = index /numbers
        val cellColumn = index %numbers

        val boxRowStart = (cellRow/boxHeight)*boxHeight
        val boxColStart = (cellColumn/boxWidth)*boxWidth

        // in this loop, the notes are set based after the random number that has been added
        for(i in 0 until numbers){
            notes.clearNote(i*numbers + cellColumn, value)
            notes.clearNote(cellRow*numbers + i, value)
            notes.clearNote((boxRowStart+i/boxWidth)*numbers + boxColStart+i%boxWidth, value)
//            notes[i*numbers + cellColumn] =
//            notes[cellRow*numbers + i][value -1] = false
//            notes[(boxRowStart+i/boxWidth)*numbers + boxColStart+i%boxWidth][value -1] = false
        }
    }

}