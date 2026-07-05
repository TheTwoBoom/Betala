package app.myhtl.betala.opensudoku

import ads_mobile_sdk.fe
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class SudokuGenerator (
    private var numbers: Int,
    private var numbersSqrt: Int = Math.sqrt(numbers.toDouble()).toInt(),
    private var erg: Array<IntArray> = Array(numbers){ IntArray(numbers) },
    private var notes: Array<Array<BooleanArray>> = Array(numbers) { Array(numbers){BooleanArray(numbers) {true} } },

    ){

    private fun reset() {
        erg = Array(numbers) { IntArray(numbers) { 0 } }
        notes = Array(numbers) { Array(numbers) { BooleanArray(numbers) { true } } }
    }
    fun getRandomSudoku(): SnapshotStateList<Int>{
        var sudokuList: SnapshotStateList<Int> = mutableStateListOf()
        for(i in 0 until numbers*numbers){
            sudokuList.add(erg[i/numbers][i%numbers])
        }
        return sudokuList
    }

    fun getSudokuString(): String{
        return "674058902598273614123964857341596278762481593985327146436819725859742361217635489"
    }

    fun removeNote(row: Int, column: Int, number: Int){
        if (number in 1..numbers) {
            notes[row][column][number-1] = false
        }
        else{
            println("keine valide Nummer: " + number)
        }
    }

    fun removeNotes(row: Int, column: Int, numbers: BooleanArray){
        for (i in 0 until numbers.size){
            if(notes[row][column][i] && numbers[i]){
                notes[row][column][i] = false
            }
        }
    }

    //searchest for the Cells with the fewest notes and chooses a random cell from the result
    fun getRandomCell(): Int{
        var possibleCells = mutableListOf<Int>()
        var fewestNotes = numbers
        //fewestNote.size is wrong, need to count true

        for(i in 0 until numbers*numbers){
            if (notes[i/numbers][i%numbers].count{it} < fewestNotes && notes[i/numbers][i%numbers].count{it} >= 1){
                fewestNotes = notes[i/numbers][i%numbers].count{it}
            }
        }

        for(i in 0 until numbers*numbers){
            if(notes[i/numbers][i%numbers].count{it} == fewestNotes)
                possibleCells.add(i)
        }


        if(possibleCells.isEmpty()){
            return -1
        }
        var randomCellIndex: Int = (Math.floor(Math.random()*(possibleCells.size))).toInt()
        //Error here:
        var randomCell = possibleCells.get(randomCellIndex)
        //println(randomCell)
        return randomCell
    }

    fun getRandomNumber(index: Int): Int{
        if(index == -1) return -1

        var notesAtCell = notes[index/numbers][index%numbers]
        var possibleNotes = mutableListOf<Int>()
        for(i in 0 until notesAtCell.size){
            if(notesAtCell[i] == true){
                possibleNotes.add(i)
            }
        }
        var randomNumberIndex = (Math.floor(Math.random()*(possibleNotes.size))).toInt()

        var randomNumber: Int = 0
        if(possibleNotes.size > 0){
            randomNumber = possibleNotes.get(randomNumberIndex)+1
        }
        //println(randomNumber)
        return randomNumber
    }

    fun creatRandomSudoku(){
        //difficulty and numbers in the Sudoku (change to parameter)
        var shouldFill = 50
        //numbers = 9
        //numbersSqrt = Math.sqrt(num<bers.toDouble()).toInt()

        reset()

        erg = Array(numbers) { IntArray(numbers) {0} }



        for(i in 0 until numbers*numbers){
            val randomCell: Int = getRandomCell()
            val randomNumber: Int = getRandomNumber(randomCell)

            if(randomCell == -1 || randomNumber == -1){
                println("Fehler: keine passende Lösung gefunden!")
                continue
            }

            if(erg[randomCell/numbers][randomCell%numbers] == 0){
                // place the random number
                erg[randomCell/numbers][randomCell%numbers] = randomNumber
                // no notes when a number is filled in
                notes[randomCell/numbers][randomCell%numbers] = BooleanArray(numbers) { false }

            }


            removeNotes(randomCell, randomNumber)
        }

        for(i in 0 until numbers){
            for(j in 0 until numbers){
                print(erg[i][j].toString()+" ")
            }
            println()
        }
    }


    fun removeNotes(randomCell: Int, randomNumber: Int){
        val selectedCellRow = randomCell/numbers
        val selectedCellColumn = randomCell%numbers

        // in this loop, the notes are set based after the random number that has been added
        for (i in 0 until numbers){
            for (j in 0 until numbers){
            val row = i
            val column = j

            if ( erg[row][column] == 0) {
                //remove the random number from the notes in the same row, column or (wrong)!! subgrid
                if (row == selectedCellRow || column == selectedCellColumn || (row / numbersSqrt == selectedCellRow / numbersSqrt && column / numbersSqrt == selectedCellColumn / numbersSqrt)) {
                    removeNote(row, column, randomNumber)
                }
            }
            }
        }
    }
}
