package app.myhtl.betala.opensudoku

class SudokuSolver(
    private var numbers: Int,
    private var numbersSqrt: Int = Math.sqrt(numbers.toDouble()).toInt(),
    private var data: Array<IntArray>,
    private var notes: Array<Array<BooleanArray>> = Array(numbers) { Array(numbers){BooleanArray(numbers) {true} } },
) {
    init {
        solve()
    }

    fun solve(){
        TODO()
    }
    fun getSolvedSudoku(): Array<IntArray>{
        return TODO()
    }

    fun hasConflict(): Boolean{
        return TODO()
    }

    fun hasOnlyOneSolution(): Boolean{
        return TODO()
    }
}