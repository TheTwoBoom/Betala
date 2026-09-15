package app.myhtl.betala.SudokuVarients


// this is different from SudokuRule, because it doesn't change or influence the Solution for the puzzle, it only changes the way the Sudoku can be solved
// a Variant must be added here if it has rules that aren't the same in every Sudoku
// A good example is Killer Sudoku which provides extra cages that can have a random pattern, size or number
interface SudokuVariant {
}