package app.myhtl.betala.opensudoku

import kotlin.collections.copyOf
import kotlin.math.sqrt

class SudokuSolver(
    private val inputData: Array<IntArray>,
    solveOnInit: Boolean,
    var data: Array<IntArray> = Array(inputData.size) { i -> inputData[i].copyOf() },
    private var numbers: Int = data.size,
    private var boxWidth: Int = sqrt(numbers.toDouble()).toInt(),
    private var boxHeight: Int = sqrt(numbers.toDouble()).toInt(),
    var notes: Array<Array<BooleanArray>> = Array(numbers) { Array(numbers){BooleanArray(numbers) {true} } },
    private var solvable: Boolean = false
    ) {
    init {
        if(solveOnInit){
            solve()
        }

    }

    fun setNewData(inputData: Array<IntArray>){
        this.data = Array(inputData.size) { i -> inputData[i].copyOf() }
        notes = Array(numbers) { Array(numbers){BooleanArray(numbers) {true} } }
        numbers = data.size
        solvable = false
    }

    fun solve(){
        setAllPossibleNotes()
        var foundNumber = false
        var lastTry = false
        while(true){
        //maybe also check if sudoku is already full
            for(i in 0 until numbers){
                for(j in 0 until numbers){

                    if(data[i][j] == 0){
                        if(notes[i][j].count{it} == 1){
                            for(k in 0 until numbers){
                                if(notes[i][j][k]){
                                    data[i][j] = k+1
                                    foundNumber = true
                                    removeNotes(i,j,k+1)
                                }
                            }

                        }
                    }

                }
            }
            if(foundNumber){
                foundNumber = false
                lastTry = false
            } else{
                if(!lastTry){
                    doHiddenSingles()
                    lastTry = true
                } else{
                    var counter = 0
                    for(i in 0 until numbers){
                        for(j in 0 until numbers){
                            if(data[i][j] == 0){
                                counter++
                            }
                        }
                    }
                    solvable = counter == 0
                    break
                }

            }
        }

    }

    fun removeNotes(cellRow: Int, cellColumn: Int, number: Int){
        for(i in 0 until numbers){
            notes[i][cellColumn][number -1] = false
            notes[cellRow][i][number -1] = false
            notes[(cellRow/boxHeight)*boxHeight+i/boxWidth][(cellColumn/boxWidth)*boxWidth+i%boxWidth][number -1] = false
        }
    }



    fun setAllPossibleNotes(){
        //go through all cells
        for(i in 0 until numbers){
            for(j in 0 until numbers){
                //skip cells with numbers
                if(data[i][j] != 0){
                    notes[i][j] = BooleanArray(numbers){false}
                    continue
                }

                for(k in 0 until numbers){
                    //remove number from notes if a number in the row was found
                    if(data[i][k] != 0){
                        notes[i][j][ data[i][k]-1 ] = false
                    }
                    //same for column
                    if(data[k][j] != 0){
                        notes[i][j][ data[k][j]-1 ] = false
                    }
                    //same for boxes
                    if(data[(i/boxHeight)*boxHeight+k/boxWidth][(j/boxWidth)*boxWidth+k%boxWidth] != 0){
                        notes[i][j][ data[(i/boxHeight)*boxHeight+k/boxWidth][(j/boxWidth)*boxWidth+k%boxWidth]-1 ] = false
                    }
                }
            }
        }
        doHiddenSingles()
    }

    fun doHiddenSingles(){
        for(num in 0 until numbers){
            for(j in 0 until numbers) {
                var rowCounter = 0
                var rowIndex = 0
                var colCounter = 0
                var colIndex = 0

                for(k in 0 until numbers) {
                    if (notes[j][k][num]) {
                        rowCounter++
                        rowIndex = k
                    }
                    if (notes[k][j][num]) {
                        colCounter++
                        colIndex = k
                    }
                }
                if(rowCounter == 1){
                    data[j][rowIndex] = num+1
                    removeNotes(j, rowIndex, num+1)
                }
                if(colCounter == 1){
                    data[colIndex][j] = num+1
                    removeNotes(colIndex, j, num+1)
                }
            }
        }
        //subgrid
        for(i in 0 until numbers){
            for(j in 0 until numbers) {
                var blockCounter = 0
                var blockIndexX = 0
                var blockIndexY = 0

                for(k in 0 until boxWidth) {
                    for (l in 0 until boxHeight) {
                        if (notes[ (j/boxWidth)*boxWidth+k ][ (j/boxHeight)*boxHeight+l ][i]) {
                            blockCounter++
                            blockIndexX = (j/boxWidth)*boxWidth+k
                            blockIndexY = (j/boxHeight)*boxHeight+l
                        }
                    }
                }

                if(blockCounter == 1){
                    data[blockIndexX][blockIndexY] = i+1
                    removeNotes(blockIndexX, blockIndexY, i+1)
                }
            }
        }
    }

    fun hasOnlyOneSolution(): Boolean{
        return solvable
    }
}