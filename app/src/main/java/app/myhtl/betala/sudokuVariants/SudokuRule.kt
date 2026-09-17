package app.myhtl.betala.sudokuVariants

interface SudokuRule {
    fun removeNotesWithRule(notes: IntArray, index: Int, value: Int)

}
