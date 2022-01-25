package com.example.mytipcalcuator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mytipcalcuator.components.InputField
import com.example.mytipcalcuator.ui.theme.MyTipCalcuatorTheme
import com.example.mytipcalcuator.util.calculate
import com.example.mytipcalcuator.util.calculateTotalTip
import com.example.mytipcalcuator.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTipCalcuatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                      
                        MainContent()
                    }


                }
            }
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(5.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFBD9FD5)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "total per person",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h5

            )

            Text(
                text = "$ $total",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h4
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    totalBillState: MutableState<String>,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,

    onValChange: (String) -> Unit
) {

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val sliderPosition = remember { (mutableStateOf(0F)) }


    val keyboardController = LocalSoftwareKeyboardController.current

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }


    TopHeader(
        totalPerPerson = totalPerPersonState.value,
    )

    Surface(
        modifier = modifier
            .padding(3.dp)
            .fillMaxWidth()
            .height(250.dp),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {

        Column {

            InputField(
                valueState = totalBillState,
                labelId = "enter bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                })
            if (validState) {
                Row(
                    modifier = modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "split",
                        modifier = modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                    )
                    Spacer(modifier = modifier.width(120.dp))
                    Row(
                        modifier = modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value = if (splitByState.value > 1) {
                                    splitByState.value - 1
                                } else 1

                                totalPerPersonState.value =
                                    calculate(
                                        totalBill = totalBillState.value.toDouble(),
                                        split = splitByState.value,
                                        tipPersent = (sliderPosition.value * 100).toInt()
                                    )
                            })

                        Text(
                            text = "${splitByState.value}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 9.dp)
                        )

                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                splitByState.value =
                                    splitByState.value + 1

                                totalPerPersonState.value =
                                    calculate(
                                        totalBill = totalBillState.value.toDouble(),
                                        split = splitByState.value,
                                        tipPersent = (sliderPosition.value * 100).toInt()
                                    )

                            })

                    }
                }

                Row(
                    modifier = modifier.padding(horizontal = 3.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    Text(
                        text = "tip",
                        modifier = modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                    )

                    Spacer(modifier = modifier.width(190.dp))
                    Text(
                        text = " $${tipAmountState.value}",
                        modifier = Modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                    )
                }

                Column(
                    modifier = modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    val tipPersent = (sliderPosition.value * 100).toInt()
                    Text(
                        text = "$tipPersent %",
                        modifier = modifier.align(
                            alignment = Alignment.CenterHorizontally
                        )
                    )

                    Spacer(modifier = modifier.height(14.dp))

                    Slider(value = sliderPosition.value,
                        onValueChange = { newValue ->
                            sliderPosition.value = newValue
                            if (totalBillState.value.toDouble() > 1 && tipPersent > 1) {
                                tipAmountState.value = calculateTotalTip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPersent = tipPersent
                                )
                            }

                            totalPerPersonState.value =
                                calculate(
                                    totalBill = totalBillState.value.toDouble(),
                                    split = splitByState.value,
                                    tipPersent = tipPersent
                                )
                        },
                        steps = 19,
                        onValueChangeFinished = {
                            Log.d(
                                "Test",
                                "BillForm onValueChangeFinished: finished.. "
                            )
                        }
                    )


                }
            }
        }
    }
}


@ExperimentalComposeUiApi
@Composable
fun MainContent() {
    val splitByState = remember {
        mutableStateOf(1)
    }
    val tipAmountState = remember { mutableStateOf(0.0) }
    val totalBillState = remember {
        mutableStateOf("")
    }

    BillForm(
        splitByState = splitByState,
        tipAmountState = tipAmountState,
        totalBillState = totalBillState
    ) {
        Log.d("Test", "MainContent: $it")
    }
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyTipCalcuatorTheme {
        Column() {
            // TopHeader()
            MainContent()
        }

    }
}