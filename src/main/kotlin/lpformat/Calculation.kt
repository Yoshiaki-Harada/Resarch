package lpformat

enum class Calculation (val str: String) {
    //<と<=，>と>=は同じ意味っぽい
    PLUS("+"), MINUS("-"),MULTI(""),DIVIDE("/")
}