package com.cw.ap

import java.math.BigDecimal
import java.math.RoundingMode

class Main {


}

fun main() {

    var d: Double
    var a: Double = 27.34
    var b: Double = 1.0

    d = a - b;

//    print("${a - b}  $d")
//    print(d.toString())
    println(BigDecimal("27.34").setScale(2, RoundingMode.DOWN))
    println(BigDecimal.valueOf(a))
    println(BigDecimal(27.34).setScale(2, RoundingMode.DOWN))


    shiftInfo.memberUseBalance = BigDecimal.valueOf(shiftInfo.memberUseBalance).add(BigDecimal.valueOf(amounts[index])).toString()

}