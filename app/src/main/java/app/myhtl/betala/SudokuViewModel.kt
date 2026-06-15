package app.myhtl.betala

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import app.myhtl.betala.opensudoku.GameManager

class SudokuViewModel : ViewModel() {
    var selectedIndex by mutableIntStateOf(0)
    var currentGame by mutableStateOf<GameManager.SudokuGame?>(null)
    var isNoteMode by mutableStateOf(false)
    var isFinishedAndCorrect by mutableStateOf(false)

    fun setIndex(index: Int) {
        selectedIndex = index
    }

    fun onNumberSelected(number: Int){
        //überprüft, ob die Zahl eine fix vorgegebene Zahl ist
        if(!isNoteMode) {
            currentGame?.changeValue(selectedIndex, number)

        } else {
            if (isNoteMode){
                currentGame?.toggleNote(selectedIndex, number)
            }
        }

        updateIsFinishedAndCorrect()
    }

    fun toggleNoteMode(){
        isNoteMode = !isNoteMode
    }

    fun eraseCell(){

        currentGame?.clearDataAt(selectedIndex)
        currentGame?.clearNotes(selectedIndex)
    }

    fun sameValue(index: Int): Boolean{
        return currentGame?.data[index] != 0 && currentGame?.data[index] == currentGame?.data[selectedIndex]
    }

    fun isEditable(index: Int): Boolean{
        return currentGame?.getOriginal()[index] == 0

    }

    fun validateSudoku(index: Int): Boolean{
        //wichtig: funktioniert noch nicht richtig, da checkCorrect anstatt wie jetzt live zu überprüfen, eigentlich mit einer fertigen gelösten Liste überprüfen sollte
        if (currentGame?.data[index] == 0) return true
        return currentGame?.checkCorrect()[index] == 0
    }

    fun updateIsFinishedAndCorrect(){
        isFinishedAndCorrect = currentGame?.isFullyCorrect == true
    }
}