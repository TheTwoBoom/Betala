package app.myhtl.betala.sudokuVariants

import app.myhtl.betala.opensudoku.clearNote

class NonConsecutiveRule(private val numbers: Int) : SudokuRule {

//    override fun removeNotesWithRule(
//        notes: Array<Array<BooleanArray>>, index: Int, value: Int
//    ) {
//        val cellRow = index /numbers
//        val cellColumn = index %numbers
//
//        val removeAbove = value < numbers
//        val removeBelow = value > 1
//
//        val offsetsX = arrayOf(0, 1, 0, -1)
//        val offsetsY = arrayOf(-1, 0, 1, 0)
//        for (i in 0 until 4){
//            val row = cellRow + offsetsX[i]
//            val col = cellColumn + offsetsY[i]
//            if(row in 0 until numbers && col in 0 until numbers){
//                if(removeAbove) notes[row][col][value] = false
//                if(removeBelow) notes[row][col][value-2] = false
//            }
//        }
//    }

    override fun removeNotesWithRule(
        notes: IntArray, index: Int, value: Int
    ) {
        val cellRow = index /numbers
        val cellColumn = index %numbers

        val removeAbove = value < numbers
        val removeBelow = value > 1

        val offsetsX = arrayOf(0, 1, 0, -1)
        val offsetsY = arrayOf(-1, 0, 1, 0)
        for (i in 0 until 4){
            val row = cellRow + offsetsX[i]
            val col = cellColumn + offsetsY[i]
            if(row in 0 until numbers && col in 0 until numbers){
                val removeIndex = row*numbers + col
                if(removeAbove) notes.clearNote(removeIndex, value + 1)
                if(removeBelow) notes.clearNote(removeIndex, value - 1)
            }
        }
    }
}

class KnightsMoveRule(private val numbers: Int) : SudokuRule{
//    override fun removeNotesWithRule(
//        notes: Array<Array<BooleanArray>>, index: Int, value: Int
//    ) {
//        val cellRow = index / numbers
//        val cellColumn = index % numbers
//
//        val offsetsX = arrayOf(-2, -1, 1, 2, 2, 1, -1, -2)
//        val offsetsY = arrayOf(-1, -2, -2, -1, 1, 2, 2, 1)
//        for (i in 0 until 8){
//            val row = cellRow + offsetsX[i]
//            val col = cellColumn + offsetsY[i]
//            if(row in 0 until numbers && col in 0 until numbers){
//                notes[row][col][value-1] = false
//            }
//        }
//    }

    override fun removeNotesWithRule(
        notes: IntArray, index: Int, value: Int
    ) {
        val cellRow = index / numbers
        val cellColumn = index % numbers

        val offsetsX = arrayOf(-2, -1, 1, 2, 2, 1, -1, -2)
        val offsetsY = arrayOf(-1, -2, -2, -1, 1, 2, 2, 1)
        for (i in 0 until 8){
            val row = cellRow + offsetsX[i]
            val col = cellColumn + offsetsY[i]
            if(row in 0 until numbers && col in 0 until numbers){
                notes.clearNote(row*numbers + col, value)
            }
        }
    }
}