package app.myhtl.betala.SudokuVarients

interface SudokuRule {
    fun removeNotesWithRule(notes: IntArray, index: Int, value: Int)

}
