package app.myhtl.betala.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList

data class FilterOption(
    val id: String, var displayName: String, var options: SnapshotStateList<FilterEntry>
)
class FilterEntry(
    val id: String,
    val label: String,
    initialIsSelected: Boolean
) {
    var isSelected: Boolean by mutableStateOf(initialIsSelected)
}