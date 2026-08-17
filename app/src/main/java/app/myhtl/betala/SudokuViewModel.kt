package app.myhtl.betala

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myhtl.betala.opensudoku.Difficulty
import app.myhtl.betala.opensudoku.GameManager
import app.myhtl.betala.opensudoku.Variant
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SudokuMode{
    GENERATOR,
    CREATOR
}
class SudokuViewModel : ViewModel() {
    var sudokuMode by mutableStateOf(SudokuMode.GENERATOR)
    var difficulty by mutableStateOf(Difficulty.Easy)
    //var size by mutableStateOf(Size.Classic) // not needed
    var variant by mutableStateOf(Variant.Classic)

    var selectedIndex by mutableIntStateOf(0)
    var selectedIndices by mutableStateOf(setOf<Int>())

    var currentGame by mutableStateOf<GameManager.SudokuGame?>(null)
    val originalList = currentGame?.originalList
    var gameSize by mutableIntStateOf(currentGame?.size ?: 0)
    var isNoteMode by mutableStateOf(false)
    var isFinishedAndCorrect by mutableStateOf(false)
    var lifeCount by mutableIntStateOf(3)
    var errorArray = BooleanArray(gameSize*gameSize){false}

    //timer
    private val _seconds = MutableStateFlow(0)
    val seconds = _seconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private var timer: Job? = null
    private var expectedTargetTime = 0L
    private var msAfterPaused = 0L
    private var msAfterLastStart = 0L

    fun startNewGame(game: GameManager.SudokuGame){
        pauseOrResumeTimer()
        currentGame = game
        gameSize = game.size
        errorArray = BooleanArray(game.size*game.size){false}
        selectedIndex = game.size*game.size/2
        selectedIndices = emptySet()
        isNoteMode = false
        isFinishedAndCorrect = false
        lifeCount = 3
    }

    fun continueGame(game: GameManager.SudokuGame, isFinished: Boolean = false, lifeCount: Int, errorArray: BooleanArray = BooleanArray(currentGame?.size ?: 0){false}, time: Int){
        pauseOrResumeTimer()
        currentGame = game
        gameSize = game.size
        this.errorArray = errorArray
        selectedIndex = game.size*game.size/2
        selectedIndices = emptySet()
        isNoteMode = false
        isFinishedAndCorrect = isFinished
        this.lifeCount = lifeCount
    }

    fun leaveGame(){
        stopAndResetTimer()
        //save here
    }

    fun setIndex(index: Int) {
        selectedIndices = emptySet()
        selectedIndex = index
    }

    fun addIndex(index: Int){
        selectedIndices += index
    }

    fun onNumberSelected(number: Int){
        //überprüft, ob die Zahl eine fix vorgegebene Zahl ist
        if(!isNoteMode) {
            currentGame?.changeValue(selectedIndex, number)
            currentGame?.changeValues(selectedIndices, number)

            validateSudoku()
        } else {
            currentGame?.toggleNote(selectedIndex, number)
            currentGame?.toggleNotes(selectedIndices, number)
        }

        updateIsFinishedAndCorrect()
    }

    fun toggleNoteMode(){
        isNoteMode = !isNoteMode
    }

    fun eraseCell(){
        currentGame?.clearDataAt(selectedIndex)
        currentGame?.clearDataAt(selectedIndices)

        currentGame?.clearNotes(selectedIndex)
        currentGame?.clearNotes(selectedIndices)
        validateSudoku()
    }

    fun sameValue(value: Int): Boolean{
        return value != 0 && value == currentGame?.data[selectedIndex]
    }

    fun isEditable(index: Int): Boolean{
        return originalList?.get(index) == 0
    }


    fun validateSudoku(){
        val oldMistakes = errorArray.count{ it }
            val checkCorrect = currentGame?.checkCorrect()
            for(i in 0 until gameSize*gameSize){
                errorArray[i] = checkCorrect?.get(i) != 0
            }
        val mistakes = errorArray.count{ it }
        if(mistakes > oldMistakes){
            lifeCount--
        }
    }

    fun hasError(index: Int): Boolean{
        return errorArray[index]
    }


    fun finishedNumbers(): BooleanArray{
        val finishedNumbers = IntArray(gameSize)
        currentGame?.data?.forEach { value ->
            if(value != 0) finishedNumbers[value - 1]++
        }
        return BooleanArray(gameSize){ i -> finishedNumbers[i] >= gameSize }
    }

    fun updateIsFinishedAndCorrect(){
        isFinishedAndCorrect = currentGame?.isFullyCorrect == true
    }

    fun pauseOrResumeTimer(){
        if(_isRunning.value){
            _isRunning.value = false
            timer?.cancel()
            msAfterPaused += System.currentTimeMillis() - msAfterLastStart
        }
        else{
           _isRunning.value = true
            msAfterLastStart = System.currentTimeMillis()

            timer = viewModelScope.launch {
                while(_isRunning.value){
                    val elapsedTime = System.currentTimeMillis()  - msAfterLastStart
                    val totalMS = msAfterPaused + elapsedTime

                    _seconds.value = (totalMS / 1000).toInt()

                    delay(1000L)
                }
            }
        }
    }

    fun stopAndResetTimer(){
        _isRunning.value = false
        timer?.cancel()
        _seconds.value = 0
        msAfterPaused = 0L
        msAfterLastStart = 0L
    }
}