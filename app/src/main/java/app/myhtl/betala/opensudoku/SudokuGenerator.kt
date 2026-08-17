package app.myhtl.betala.opensudoku

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlin.math.floor
import kotlin.math.sqrt

class SudokuGenerator (
    private var numbers: Int,
    val boxWidth: Int = sqrt(numbers.toDouble()).toInt(),
    val boxHeight: Int = sqrt(numbers.toDouble()).toInt(),
    private var erg: Array<IntArray> = Array(numbers){ IntArray(numbers) },
    private var notes: Array<Array<BooleanArray>> = Array(numbers) { Array(numbers){BooleanArray(numbers) {true} } },
    private val difficulty: Difficulty
    ){

    private fun reset() {
        erg = Array(numbers) { IntArray(numbers) }
        notes = Array(numbers) { Array(numbers) { BooleanArray(numbers) { true } } }
    }
    fun getRandomSudoku(): SnapshotStateList<Int>{
        var isValid = false
        //current solution for valid Sudokus without backtracking
        while (!isValid){
            isValid = createRandomFullySolvedSudoku()
        }

        removeRandomNumbers(difficulty.calculateNumbersToRemove(numbers*numbers))


        val sudokuList: SnapshotStateList<Int> = mutableStateListOf()
        for(i in 0 until numbers*numbers){
            sudokuList.add(erg[i/numbers][i%numbers])
        }
        return sudokuList
    }

    //searchest for the Cells with the fewest notes and chooses a random cell from the result
    fun getRandomCell(): Int {
        var fewestNotes = numbers
        var countCellsWithFewest = 0

        for (i in 0 until numbers * numbers) {
            val row = i / numbers
            val col = i % numbers
            if (erg[row][col] == 0) {
                val notes = notes[row][col].count { it }
                if (notes in 1 until fewestNotes) {
                    fewestNotes = notes
                    countCellsWithFewest = 1
                } else if (notes == fewestNotes) {
                    countCellsWithFewest++
                }
            }

        }

        if (countCellsWithFewest == 0) {
            return -1
        }

        val randomCellIndex: Int = (floor(Math.random() * (countCellsWithFewest))).toInt()

        var counter = 0
        for (i in 0 until numbers * numbers) {
            val row = i / numbers
            val col = i % numbers
            if (notes[row][col].count { it } == fewestNotes) {
                if (counter == randomCellIndex) {
                    return i
                }
                counter++
            }
        }
        return -1
    }

    fun getRandomNumber(index: Int): Int{
        if(index == -1) return -1

        val notesAtCell = notes[index/numbers][index%numbers]
        val possibleNotes = notesAtCell.count{it}
        val randomNumberIndex = (floor(Math.random() * (possibleNotes))).toInt()

        if(possibleNotes < 0){
            return -1
        }
        else{
            var counter = 0
            for(i in 0 until numbers){
                if(notesAtCell[i]){
                    if(counter == randomNumberIndex){
                        return i + 1
                    }
                    counter++
                }
            }
        }
        return -1
    }


    fun createRandomFullySolvedSudoku(): Boolean{
        reset()

        erg = Array(numbers) { IntArray(numbers) }

        for(i in 0 until numbers*numbers){
            val randomCell: Int = getRandomCell()
            val randomNumber: Int = getRandomNumber(randomCell)

            if(randomCell == -1 || randomNumber == -1){
                println("Fehler: keine passende Lösung gefunden!")
                //isValid?
                return false
            }

            if(erg[randomCell/numbers][randomCell%numbers] == 0){
                // place the random number
                erg[randomCell/numbers][randomCell%numbers] = randomNumber
                // no notes when a number is filled in
                notes[randomCell/numbers][randomCell%numbers] = BooleanArray(numbers) { false }
            }

            removeNotes(randomCell, randomNumber)
        }

        //for(i in 0 until numbers){
        //    for(j in 0 until numbers){
        //        print(erg[i][j].toString()+" ")
        //    }
        //    println()
        //}
        return true
    }


    fun removeNotes(randomCell: Int, randomNumber: Int){
        val cellRow = randomCell/numbers
        val cellColumn = randomCell%numbers

        // in this loop, the notes are set based after the random number that has been added
        for(i in 0 until numbers){
            notes[i][cellColumn][randomNumber -1] = false
            notes[cellRow][i][randomNumber -1] = false
            notes[(cellRow/boxHeight)*boxHeight+i/boxWidth][(cellColumn/boxWidth)*boxWidth+i%boxWidth][randomNumber -1] = false
        }
    }

    fun removeRandomNumbers(amount: Int){
        val solver = SudokuSolver(
            solveOnInit = false,
            inputData = erg,
            boxWidth = boxWidth,
            boxHeight = boxHeight
        )
        repeat(amount){
            val randomCell = getRandomFilledCell()
            val rememberCell = erg[randomCell/numbers][randomCell%numbers]
            erg[randomCell/numbers][randomCell%numbers] = 0
            solver.setNewData(erg)
            solver.solve()

            // if it isn't solvable
            if(!solver.hasOnlyOneSolution()){
                erg[randomCell/numbers][randomCell%numbers] = rememberCell
                //it tries different cells
                for(i in 0 until numbers){

                    val randomCell = getRandomFilledCell()
                    val rememberCell = erg[randomCell/numbers][randomCell%numbers]
                    erg[randomCell/numbers][randomCell%numbers] = 0
                    solver.setNewData(erg)
                    solver.solve()

                    if(!solver.hasOnlyOneSolution()){
                        erg[randomCell/numbers][randomCell%numbers] = rememberCell

                    }
                    else{
                        break
                    }

                    if(i == numbers-1){
                        println("Not all numbers were removed")
                        return
                    }

                }


            }
        }
    }

    fun getRandomFilledCell(): Int{
        var countFilled = 0

        for (i in 0 until numbers * numbers) {
            if (erg[i / numbers][i % numbers] != 0) {
                countFilled++
            }
        }

        if (countFilled == 0) {
            return -1
        }

        val randomCellIndex: Int = (floor(Math.random() * (countFilled))).toInt()

        var counter = 0
        for (i in 0 until numbers * numbers) {
            if (erg[i / numbers][i % numbers] != 0) {
                if (counter == randomCellIndex) {
                    return i
                }
                counter++
            }
        }
        return -1
    }
}
