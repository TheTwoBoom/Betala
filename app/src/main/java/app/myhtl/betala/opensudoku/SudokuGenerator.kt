package app.myhtl.betala.opensudoku

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.myhtl.betala.SudokuVarients.SudokuRule
import kotlin.math.floor
import kotlin.math.sqrt

class SudokuGenerator (
    private var numbers: Int,
    private val boxWidth: Int = sqrt(numbers.toDouble()).toInt(),
    private val boxHeight: Int = sqrt(numbers.toDouble()).toInt(),
    private var data: Array<IntArray> = Array(numbers){ IntArray(numbers) },
    private val ruleSet: List<SudokuRule>,
    private var notes: Array<Array<BooleanArray>> = Array(numbers) { Array(numbers){BooleanArray(numbers) {true} } },
    private val difficulty: Difficulty
) {

    private fun reset() {
        data = Array(numbers) { IntArray(numbers) }
        notes = Array(numbers) { Array(numbers) { BooleanArray(numbers) { true } } }
    }

    fun getRandomSudoku(): SnapshotStateList<Int> {
        var isValid = false
        // current solution for valid Sudokus without backtracking (generate until valid)
        while (!isValid) {
            isValid = createRandomFullySolvedSudoku()
        }

        removeRandomNumbers(difficulty.calculateNumbersToRemove(numbers * numbers))


        val sudokuList: SnapshotStateList<Int> = mutableStateListOf()
        for(i in 0 until numbers*numbers){
            sudokuList.add(data[i/numbers][i%numbers])
        }
        return sudokuList
    }

    // search for the cells with the fewest notes and chooses a random cell from the result
    fun getRandomCell(): Int {
        var fewestNotes = numbers
        var countCellsWithFewest = 0

        for (i in 0 until numbers * numbers) {
            val row = i / numbers
            val col = i % numbers
            if (data[row][col] == 0) {
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

    fun getRandomNumber(index: Int): Int {
        if (index == -1) return -1

        val notesAtCell = notes[index / numbers][index % numbers]
        val possibleNotes = notesAtCell.count { it }
        val randomNumberIndex = (floor(Math.random() * (possibleNotes))).toInt()

        if (possibleNotes < 0) {
            return -1
        } else {
            var counter = 0
            for (i in 0 until numbers) {
                if (notesAtCell[i]) {
                    if (counter == randomNumberIndex) {
                        return i + 1
                    }
                    counter++
                }
            }
        }
        return -1
    }


    fun createRandomFullySolvedSudoku(): Boolean {
        reset()
        for (i in 0 until numbers * numbers) {
            val randomCell: Int = getRandomCell()
            val randomNumber: Int = getRandomNumber(randomCell)

            if (randomCell == -1 || randomNumber == -1) {
                println("Fehler: No solution found!")
                //isValid?
                return false
            }

                // place the random number
                data[randomCell/numbers][randomCell%numbers] = randomNumber
                // no notes when a number is filled in
                //if you want to delete this, add it in the removeNotes
                notes[randomCell/numbers][randomCell%numbers] = BooleanArray(numbers) { false }

//            for (rule in ruleSet){
//                rule.removeNotesWithRule(notes, randomCell, randomNumber)
//            }
            removeNotes(randomCell, randomNumber)
        }
        return true
    }

    fun createSudokuRecursive(): Boolean{
        val nextIndex = getRandomCell()

        if(nextIndex == -1) return true

        val notes = getNotesFromCell(nextIndex).toList().shuffled()

        for(note in notes){
           data[nextIndex/numbers][nextIndex%numbers] = note

            if(createSudokuRecursive()) return true

            data[nextIndex/numbers][nextIndex%numbers] = 0
            //TODO restore notes
        }

        return false
    }

    fun getNotesFromCell(index: Int): List<Int>{
        val notes = notes[index/numbers][index%numbers]
        val list = mutableListOf<Int>()
        for(index in notes.indices){
            if(notes[index]){
                list.add(index + 1)
            }
        }
        return list
    }

    fun removeNotes(index: Int, value: Int){
        val cellRow = index /numbers
        val cellColumn = index %numbers

        // in this loop, the notes are set based after the random number that has been added
        for(i in 0 until numbers){
            notes[i][cellColumn][value -1] = false
            notes[cellRow][i][value -1] = false
            notes[(cellRow/boxHeight)*boxHeight+i/boxWidth][(cellColumn/boxWidth)*boxWidth+i%boxWidth][value -1] = false
        }
    }

    fun removeRandomNumbers(amount: Int) {
        val solver = SudokuSolver(
            solveOnInit = false,
            inputData = data,
            boxWidth = boxWidth,
            boxHeight = boxHeight,
            ruleSet = ruleSet
        )
        repeat(amount) {
            val randomCell = getRandomFilledCell()
            //if (randomCell == -1) return
            val rememberCell = data[randomCell/numbers][randomCell%numbers]
            data[randomCell/numbers][randomCell%numbers] = 0
            solver.setNewData(data)
            solver.solve()

            // if it isn't solvable
            if(!solver.hasOnlyOneSolution()){
                data[randomCell/numbers][randomCell%numbers] = rememberCell
                //it tries different cells
                for (i in 0 until numbers) {

                    val randomCell = getRandomFilledCell()
                    val rememberCell = data[randomCell/numbers][randomCell%numbers]
                    data[randomCell/numbers][randomCell%numbers] = 0
                    solver.setNewData(data)
                    solver.solve()

                    if(!solver.hasOnlyOneSolution()){
                        data[randomCell/numbers][randomCell%numbers] = rememberCell

                    } else {
                        break
                    }

                    if (i == numbers - 1) {
                        println("Not all numbers were removed")
                        return
                    }

                }


            }
        }
    }

    fun getRandomFilledCell(): Int {
        var countFilled = 0

        for (i in 0 until numbers * numbers) {
            if (data[i / numbers][i % numbers] != 0) {
                countFilled++
            }
        }

        if (countFilled == 0) {
            return -1
        }

        val randomCellIndex: Int = (floor(Math.random() * (countFilled))).toInt()

        var counter = 0
        for (i in 0 until numbers * numbers) {
            if (data[i / numbers][i % numbers] != 0) {
                if (counter == randomCellIndex) {
                    return i
                }
                counter++
            }
        }
        return -1
    }
}
