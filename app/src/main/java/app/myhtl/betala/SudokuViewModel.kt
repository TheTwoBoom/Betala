package app.myhtl.betala

import android.app.Application
import android.app.GameManager
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.myhtl.betala.sudokuVariants.ClassicSudokuRule
import app.myhtl.betala.opensudoku.Difficulty
import app.myhtl.betala.opensudoku.Sudoku
import app.myhtl.betala.opensudoku.SudokuGeneratorV2
import app.myhtl.betala.opensudoku.Variant
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import app.myhtl.betala.utils.SettingUtils
import java.time.LocalDateTime
import java.util.Stack
import kotlin.collections.copyOf

enum class SudokuMode {
    GENERATOR,
    CREATOR
}

class SudokuViewModel(application: Application) : AndroidViewModel(application) {
    private var moveHistory = Stack<SudokuMove>()
    private var canUndo by mutableStateOf(false)
    private var moveFuture = Stack<SudokuMove>()
    private var canRedo by mutableStateOf(false)
    var currentSudoku by mutableStateOf<Sudoku?>(null)
    //currentGame updates itself automatically
    val currentGame by derivedStateOf { currentSudoku?.game }
    var sudokuMode by mutableStateOf(SudokuMode.GENERATOR)

    //var size by mutableStateOf(Size.Classic) // not needed

    var selectedIndex by mutableIntStateOf(0)
    var selectedIndices by mutableStateOf(setOf<Int>())
    var gameSize by mutableIntStateOf(currentGame?.size ?: 0)
    var isNoteMode by mutableStateOf(false)
    var isFinishedAndCorrect by mutableStateOf(false)
    var errorArray = BooleanArray(gameSize * gameSize) { false }

    private val context get() = getApplication<Application>()

    /** Check if note mode is disallowed by settings */
    private val isNoteModeDisabled: Boolean
        get() = SettingUtils(context).getBool("notemode") == true

    /** Check if validation is disabled by settings */
    private val isValidationDisabled: Boolean
        get() = SettingUtils(context).getBool("notemode") == true


    var isGenerating by mutableStateOf(false)

    //timer
    private val _seconds = MutableStateFlow(0)
    val seconds = _seconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private var timer: Job? = null
    private var expectedTargetTime = 0L
    private var msAfterPaused = 0L
    private var msAfterLastStart = 0L

//generates if generate is true or empty
    fun generateAndStartNewGame(numbers: Int, boxWith: Int, boxHeight: Int, difficulty: Difficulty, variants: Set<Variant>, sudokuName: String, generate: Boolean){
        moveHistory.clear()
        moveFuture.clear()
        updateUndoRedoFlags()
        // for safety leave old game
        leaveGame()
        pauseOrResumeTimer()

        if(generate){
            viewModelScope.launch {
                isGenerating = true

                val generator = SudokuGeneratorV2(
                    numbers = numbers,
                    ruleSet = listOf(ClassicSudokuRule(
                        numbers = numbers,
                        boxWidth = boxWith,
                        boxHeight = boxHeight
                    )),
                    difficulty = difficulty
                )

                //test:
                //generator.testSudokuGeneratorTime(1000)

                val sudoku = generator.getNewSudoku()

                val metadata = Sudoku.MetaData(
                    name = sudokuName,
                    difficulty = difficulty,
                    author = "",
                    creationTime = LocalDateTime.now()
                )

                val game = Sudoku.Game(
                    data = sudoku,
                    size = sudoku.size,
                    //name = sudokuName,
                    boxWidth = boxWith,
                    boxHeight = boxHeight,
                    variants = variants,
                )

                gameSize = game.size
                errorArray = BooleanArray(game.size*game.size){false}
                selectedIndex = game.size*game.size/2
                selectedIndices = emptySet()
                isNoteMode = false
                isFinishedAndCorrect = false
                //lifeCount = 3

                isGenerating = false
            }
        } else{
            //empty Sudoku

            val metadata = Sudoku.MetaData(
                name = sudokuName,
                author = "your sudoku",//stringResource(R.string.create_header),
                difficulty = difficulty,
                creationTime = LocalDateTime.now(),
            )

            val game = Sudoku.Game(
                size = numbers,
                boxWidth = boxWith,
                boxHeight = boxHeight,
                variants = variants,
                isFullyCorrect = false
            )

            currentSudoku = Sudoku(
                metadata = metadata,
                game = game
            )
        }
    }

//    fun startNewGame(openSudoku: GameManager.OpenSudoku){
//        moveHistory.clear()
//        moveFuture.clear()
//        updateUndoRedoFlags()
//        // for safety leave old game
//        leaveGame()
//        pauseOrResumeTimer()
//        opnSudoku = openSudoku
//        gameSize = openSudoku.games[0].size
//        errorArray = BooleanArray(openSudoku.games[0].size * openSudoku.games[0].size) { false }
//        selectedIndex = openSudoku.games[0].size * openSudoku.games[0].size / 2
//        selectedIndices = emptySet()
//        isNoteMode = false
//        isFinishedAndCorrect = false
//    }

    fun continueGame(
        sudoku: Sudoku,
        moveHistory: Stack<SudokuMove>,
        moveFuture: Stack<SudokuMove>,
        isFinished: Boolean = false,
        errorArray: BooleanArray = BooleanArray(
            currentGame?.size ?: 0
        ) { false },
        time: Int
    ) {
        currentSudoku = sudoku
        this.moveHistory = moveHistory
        this.moveFuture = moveFuture
        updateUndoRedoFlags()
        // for safety leave old game
        leaveGame()
        //TODO() update timer to Duration!
        setTime(0)
        pauseOrResumeTimer()
        gameSize = currentGame?.size ?: return
        this.errorArray = errorArray
        selectedIndex = gameSize * gameSize / 2
        selectedIndices = emptySet()
        isNoteMode = false
        isFinishedAndCorrect = isFinished
    }

    fun leaveGame() {
        stopAndResetTimer()
        // TODO() Save game
    }

    fun setIndex(index: Int) {
        selectedIndices = emptySet()
        selectedIndex = index
    }

    fun addIndex(index: Int) {
        selectedIndices += index
    }

    fun onNumberSelected(number: Int) {
        val game = currentGame ?: return
        val indices = selectedIndices + selectedIndex

        val oldMove = getCurrentMove()

        var valueChanged = true
        if (!isNoteMode && selectedIndices.isEmpty()) {
            valueChanged = game.changeValues(indices, number)

            if (valueChanged) validateSudoku(indices)
        } else if(!isNoteModeDisabled) {
            valueChanged = game.toggleNotes(indices, number)
        }

        if (!valueChanged) return

        pushMoveToHistory(oldMove)
        updateIsFinishedAndCorrect()
        updateUndoRedoFlags()
    }

    fun toggleNoteMode() {
        // If note mode is disabled in settings, prevent toggling it on
        if (!isNoteModeDisabled) {
            isNoteMode = !isNoteMode
        }
    }

    fun eraseCell() {

        val game = currentGame ?: return
        val indices = selectedIndices + selectedIndex

        val oldMove = getCurrentMove()

        val valueChanged = game.clearDataAt(indices)
        val noteChanged = game.clearNotes(indices)
        if (!valueChanged && !noteChanged) return

        pushMoveToHistory(oldMove)
        validateSudoku(indices)
        updateUndoRedoFlags()
    }

    fun sameValue(value: Int): Boolean {
        return value != 0 && value == currentGame?.data[selectedIndex]
    }

    fun isEditable(index: Int): Boolean {
        return currentGame?.originalList?.get(index) == 0
    }


    fun validateSudoku(affectedIndices: Set<Int> = emptySet(), changeLives: Boolean = true) {
        val checkCorrect = currentGame?.checkCorrect()
        for (i in 0 until gameSize * gameSize) {
            errorArray[i] = checkCorrect?.get(i) != 0
        }
        if (!changeLives) return
        if (affectedIndices.any { index ->
                errorArray[index] && currentGame?.data[index] != 0
            }) currentSudoku?.userData?.lifeCount--
    }

    fun hasError(index: Int): Boolean {
        return errorArray[index]
    }


    fun finishedNumbers(): BooleanArray {
        val finishedNumbers = IntArray(gameSize)
        currentGame?.data?.forEach { value ->
            if (value != 0) finishedNumbers[value - 1]++
        }
        return BooleanArray(gameSize) { i -> finishedNumbers[i] >= gameSize }
    }

    fun updateIsFinishedAndCorrect() {
        isFinishedAndCorrect = currentGame?.isFullyCorrect == true
    }


    fun undoMove() {
        val game = currentGame ?: return
        if (moveHistory.isEmpty()) return

        val lastMove = moveHistory.pop()

        val redoStates = lastMove.moves.map { state ->
            CellState(
                index = state.index,
                value = game.data[state.index],
                notes = game.noteData[state.index].copyOf()
            )
        }
        moveFuture.push(SudokuMove(redoStates))

        lastMove.moves.forEach { state ->
            game.data[state.index] = state.value
            game.noteData[state.index] = state.notes
        }
        validateSudoku(changeLives = false)
        updateUndoRedoFlags()
    }

    fun redoMove() {
        val game = currentGame ?: return
        if (moveFuture.isEmpty()) return

        val futureMove = moveFuture.pop()


        val undoStates = futureMove.moves.map { state ->
            CellState(
                index = state.index,
                value = game.data[state.index],
                notes = game.noteData[state.index].copyOf()
            )
        }
        moveHistory.push(SudokuMove(undoStates))

        futureMove.moves.forEach { state ->
            game.data[state.index] = state.value
            game.noteData[state.index] = state.notes
        }

        validateSudoku(changeLives = false)
        updateUndoRedoFlags()
    }

    private fun updateUndoRedoFlags() {
        canUndo = moveHistory.isNotEmpty()
        canRedo = moveFuture.isNotEmpty()
    }

    fun canUndo() = canUndo
    fun canRedo() = canRedo

    private fun getCurrentMove(): SudokuMove? {
        val game = currentGame ?: return null

        val indices = selectedIndices + selectedIndex
        val states = indices.map { index ->
            CellState(
                index = index,
                value = game.data[index],
                notes = game.noteData[index].copyOf()
            )
        }
        return SudokuMove(states)
    }

    private fun pushMoveToHistory(move: SudokuMove?) {
        moveHistory.push(move)
        moveFuture.clear()
    }

    private fun pushMoveToFuture() {
        val game = currentGame ?: return

        val indices = selectedIndices + selectedIndex
        val states = indices.map { index ->
            CellState(
                index = index,
                value = game.data[index],
                notes = game.noteData[index].copyOf()
            )
        }
        moveFuture.push(SudokuMove(states))
    }

    //timer functions
    fun pauseOrResumeTimer() {
        if (_isRunning.value) {
            _isRunning.value = false
            timer?.cancel()
            msAfterPaused += System.currentTimeMillis() - msAfterLastStart
        } else {
            _isRunning.value = true
            msAfterLastStart = System.currentTimeMillis()

            timer = viewModelScope.launch {
                while (_isRunning.value) {
                    val elapsedTime = System.currentTimeMillis() - msAfterLastStart
                    val totalMS = msAfterPaused + elapsedTime

                    _seconds.value = (totalMS / 1000).toInt()

                    delay(1000L)
                }
            }
        }
    }

    fun stopAndResetTimer() {
        _isRunning.value = false
        timer?.cancel()
        _seconds.value = 0
        msAfterPaused = 0L
        msAfterLastStart = 0L
    }

    fun setTime(sec: Int){
        _seconds.value = sec
        msAfterPaused = 0L
        msAfterLastStart = 0L
    }
}


data class SudokuMove(
    val moves: List<CellState>
)

data class CellState(
    val index: Int,
    val value: Int,
    val notes: BooleanArray
)