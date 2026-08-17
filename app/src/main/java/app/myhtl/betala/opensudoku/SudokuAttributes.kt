package app.myhtl.betala.opensudoku

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import app.myhtl.betala.R
import kotlin.math.roundToInt


enum class Difficulty (val fill: Int, @SuppressLint("SupportAnnotationUsage") @StringRes val label: Int){

    Easy(50, R.string.Difficulty_Easy),
    Medium(42, R.string.Difficulty_Medium),
    Hard(34, R.string.Difficulty_Hard),
    Extreme(26, R.string.Difficulty_Extreme);

    fun calculateNumbersToRemove(gridSize: Int): Int{
        return gridSize - ((fill/100.0) * gridSize).roundToInt()
    }
}

enum class Variant(@DrawableRes val icon: Int){
    Classic(R.drawable.numbers),
    Killer(R.drawable.dice),
    Chess(R.drawable.chess_knight)
}
//size not really needed
enum class Size(){
    Classic(),
    Killer(),
    Chess()
}