package com.example.mytipcalcuator.util

fun calculateTotalTip(totalBill: Double, tipPersent: Int): Double {

    return  if(totalBill>1 && totalBill.toString().isNotEmpty()&&totalBill!=0.0){
        (totalBill*tipPersent)/100}
    else 0.0
}

fun calculate(
    totalBill:Double,
    split:Int,
    tipPersent:Int):Double{

    val bill= calculateTotalTip(totalBill = totalBill,tipPersent = tipPersent)+totalBill
return bill/split

}