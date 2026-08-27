package app.myhtl.betala.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.myhtl.betala.AppAdditionalDestinations
import app.myhtl.betala.R
import app.myhtl.betala.SudokuMode
import app.myhtl.betala.SudokuViewModel
import app.myhtl.betala.opensudoku.Difficulty
import app.myhtl.betala.opensudoku.GameManager
import app.myhtl.betala.opensudoku.SudokuGenerator
import app.myhtl.betala.opensudoku.Variant
import com.google.common.math.IntMath.sqrt
import java.math.RoundingMode

data class DropDownItemData(

    val text: String,
    @DrawableRes val iconRes: Int,
    val enabled: Boolean = true,
    val number: Int = 0,
    val difficulty: Difficulty = Difficulty.Easy,
    val variant: Variant = Variant.Classic
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetSudokuRulesScreen(
    navController: NavController,
    sudokuViewModel: SudokuViewModel,
    //furtherNavButton: @Composable (numbers: Int, selectedBoxWidth: Int, selectedBoxHeight: Int, text: String) -> Unit
){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(
                text = stringResource(R.string.setRules_header),
                navController = navController,
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 20.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {


                val variantItems = remember {
                    listOf(
                        DropDownItemData(Variant.Classic.name, R.drawable.numbers, variant = Variant.Classic),
                        DropDownItemData(Variant.Killer.name, R.drawable.dice, enabled = false, variant = Variant.Killer),
                        DropDownItemData(Variant.Chess.name, R.drawable.chess_knight, enabled = false, variant = Variant.Chess)

                    )
                }
                Text(stringResource(R.string.selectVariants_text))
                val selectedVariant = MyDropdownMenu(variantItems)


                val sudokuSizes = mutableListOf<DropDownItemData>()
                for (i in 4..25) {
                    if (i % 2 == 0 || i % 3 == 0 || sqrt(i, RoundingMode.FLOOR) * sqrt(
                            i,
                            RoundingMode.FLOOR
                        ) == i
                    ) {
                        sudokuSizes.add(
                            DropDownItemData(
                                text = "$i * $i",
                                R.drawable.numbers,
                                number = i
                            )
                        )
                    }
                }

                Text(stringResource(R.string.selectSize_text))
                val selectedSize = MyDropdownMenu(
                    sudokuSizes,
                    selectedMenuItem = DropDownItemData(
                        text = "9 * 9",
                        R.drawable.numbers,
                        number = 9
                    )
                )
                val selectedNumber = selectedSize.number
                var selectedBoxWidth = sqrt(selectedSize.number, RoundingMode.FLOOR)
                var selectedBoxHeight = selectedBoxWidth

                //all sizes for the box
                var selectedBoxSize: DropDownItemData
                val selectedBoxMenuList: MutableList<DropDownItemData> = mutableListOf()

                for (i in 2 until 25) {
                    if (selectedNumber % i == 0 && selectedNumber / i != 1) {
                        selectedBoxMenuList.add(
                            DropDownItemData(
                                "" + selectedNumber / i + " * " + i,
                                R.drawable.edit,
                                number = selectedNumber / i
                            )
                        )

                    }
                }

                val numbers = selectedSize.number

                if (selectedBoxMenuList.size > 1) {
                    Text(stringResource(R.string.selectBoxSize_text))
                    val numbersSqrt = sqrt(numbers, RoundingMode.FLOOR)
                    if (numbersSqrt * numbersSqrt == numbers) {
                        selectedBoxSize = DropDownItemData(
                            text = "$numbersSqrt * $numbersSqrt",
                            R.drawable.edit,
                            number = numbersSqrt
                        )


                        selectedBoxSize = MyDropdownMenu(
                            selectedBoxMenuList,
                            key = selectedNumber,
                            selectedMenuItem = selectedBoxSize
                        )
                    } else {
                        selectedBoxSize = MyDropdownMenu(selectedBoxMenuList, key = selectedNumber)

                    }

                    selectedBoxWidth = selectedBoxSize.number
                    selectedBoxHeight = selectedNumber / selectedBoxSize.number
                }


                //Difficulty
                var selectedDifficulty = Difficulty.Easy
                if (sudokuViewModel.sudokuMode == SudokuMode.GENERATOR) {
                    val difficulties = mutableListOf<DropDownItemData>()
                    Difficulty.entries.forEach { value ->
                        difficulties.add(
                            DropDownItemData(
                                text = stringResource(value.label),
                                iconRes = R.drawable.numbers,
                                difficulty = value
                            )
                        )
                    }
                    Text(stringResource(R.string.selectDifficulty_text))
                    selectedDifficulty = MyDropdownMenu(difficulties).difficulty
                }






                if (sudokuViewModel.sudokuMode == SudokuMode.CREATOR) {

                    val creationText = stringResource(R.string.create_header)
                    Button(
                        onClick = {
                            sudokuViewModel.variant = selectedVariant.variant

                            val sudoku =
                                GameManager.SudokuGame(
                                    data = SnapshotStateList(numbers * numbers) { 0 },
                                    name = creationText,
                                    boxWidth = selectedBoxWidth,
                                    boxHeight = selectedBoxHeight
                                )
                            sudokuViewModel.startNewGame(sudoku)
                            navController.navigate(AppAdditionalDestinations.SUDOKU.route)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(stringResource(R.string.start_game))
                            Icon(
                                painterResource(R.drawable.play_arrow),
                                contentDescription = "Play"
                            )
                        }
                    }
                } else {
                    val creationText = stringResource(R.string.generated)
                    Button(
                        onClick = {
                            sudokuViewModel.variant = selectedVariant.variant
                            sudokuViewModel.difficulty = selectedDifficulty
                            val generator = SudokuGenerator(
                                numbers = numbers,
                                boxWidth = selectedBoxWidth,
                                boxHeight = selectedBoxHeight,
                                difficulty = selectedDifficulty
                            )
                            // would else be empty
                            val sudoku = GameManager.SudokuGame(
                                data = generator.getRandomSudoku(),
                                name = creationText,
                                boxWidth = selectedBoxWidth,
                                boxHeight = selectedBoxHeight
                            )
                            sudokuViewModel.startNewGame(sudoku)
                            navController.navigate(AppAdditionalDestinations.SUDOKU.route)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(stringResource(R.string.start_game))
                            Icon(
                                painterResource(R.drawable.play_arrow),
                                contentDescription = "Play"
                            )
                        }

                    }
                }

            }
        }
}


@Composable
fun MyOutlinedCard(modifier: Modifier, colors: CardColors = CardDefaults.outlinedCardColors(), border: BorderStroke = CardDefaults.outlinedCardBorder(), itemData: DropDownItemData){
    val newColors =
    if(!itemData.enabled)  CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
    else colors
    OutlinedCard(
        modifier = modifier,
        colors = newColors,
        border = border
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(itemData.iconRes),
                contentDescription = itemData.text
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = itemData.text
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDropdownMenu(menuItems: List<DropDownItemData>, key: Any? = null, selectedMenuItem: DropDownItemData = menuItems.first()): DropDownItemData{
    var expanded by remember { mutableStateOf(false) }

    var currentSelectedMenuItem by remember(key) {  mutableStateOf(selectedMenuItem)}

    ExposedDropdownMenuBox(
        modifier = Modifier
            .widthIn(max = 400.dp)
            .padding(horizontal = 30.dp),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ){
        MyOutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable),

            border = BorderStroke(
                width = 3.dp,
                brush = Brush.linearGradient(listOf(Color(0xFF8f48db),Color(0xFF76d5e8))),
            ),
            itemData = currentSelectedMenuItem
        )

        ExposedDropdownMenu(
            shape = RoundedCornerShape(12.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false},
            containerColor = MaterialTheme.colorScheme.surface, // Macht den Menükasten unsichtbar
            shadowElevation = 0.dp,             // Entfernt den Kasten-Schatten
            tonalElevation = 0.dp,

            ) {
            menuItems.forEach { value ->
                if(value != currentSelectedMenuItem) {

                    MyOutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable(value.enabled) {

                                expanded = false
                                currentSelectedMenuItem = value
                            },
                        itemData = value
                    )
                }
            }
        }
    }
    return   currentSelectedMenuItem
}
