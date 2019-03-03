package model

//truthfulValue, surplusValue
class Value(val tValue: Double, val sValue: Double) {
    fun getValue(): Double {
        return tValue + sValue
    }
}