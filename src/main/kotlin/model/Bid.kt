package model

//(10.0,[2, 0, 3])
//予算10.0で財0を数量2，財1を数量0, 財2を数量3希望する
class Bid(val value: Value, val bundle: List<Double>) {

    fun getValue(): Double {
        return value.getValue()
    }

}