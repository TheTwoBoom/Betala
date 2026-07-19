package app.myhtl.betala

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.myhtl.betala.opensudoku.GameManager
import app.myhtl.betala.utils.SettingUtils
import kotlinx.coroutines.launch

class SudokuViewModel(application: Application) : AndroidViewModel(application) {
    var selectedIndex by mutableIntStateOf(0)
    var currentGame by mutableStateOf<GameManager.SudokuGame?>(null)
    var isNoteMode by mutableStateOf(false)
    var isFinishedAndCorrect by mutableStateOf(false)

    private val context get() = getApplication<Application>()

    /** Check if note mode is disallowed by settings */
    private val isNoteModeDisabled: Boolean
        get() = SettingUtils(context).getBool("notemode") == true

    /** Check if validation is disabled by settings */
    private val isValidationDisabled: Boolean
        get() = SettingUtils(context).getBool("notemode") == true

    fun setIndex(index: Int) {
        selectedIndex = index
    }

    fun onNumberSelected(number: Int) {
        // If note mode is disabled in settings, treat as non-note regardless
        if (!isNoteMode || isNoteModeDisabled) {
            currentGame?.changeValue(selectedIndex, number)
        } else {
            currentGame?.toggleNote(selectedIndex, number)
        }
        updateIsFinishedAndCorrect()
    }

    fun toggleNoteMode() {
        // If note mode is disabled in settings, prevent toggling it on
        if (!isNoteModeDisabled) {
            isNoteMode = !isNoteMode
        }
    }

    fun eraseCell() {
        currentGame?.clearDataAt(selectedIndex)
        currentGame?.clearNotes(selectedIndex)
    }

    fun sameValue(index: Int): Boolean {
        return currentGame?.data[index] != 0 && currentGame?.data[index] == currentGame?.data[selectedIndex]
    }

    fun isEditable(index: Int): Boolean {
        return currentGame?.getOriginal()[index] == 0
    }

    fun validateSudoku(index: Int): Boolean {
        // If validation is disabled, always return valid
        if (isValidationDisabled) return true
        if (currentGame?.data[index] == 0) return true
        return currentGame?.checkCorrect()[index] == 0
    }

    fun updateIsFinishedAndCorrect() {
        isFinishedAndCorrect = currentGame?.isFullyCorrect == true
    }
}