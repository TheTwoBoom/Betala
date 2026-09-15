package app.myhtl.betala.opensudoku

fun IntArray.clearNote(index: Int, value: Int){
    this[index] = this[index] and (1 shl (value - 1)).inv()
}