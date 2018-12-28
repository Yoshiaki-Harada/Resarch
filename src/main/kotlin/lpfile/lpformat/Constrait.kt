package lpfile.lpformat

enum class Constrait (val str: String) {
    //<と<=，>と>=は同じ意味っぽい
    LE("<"), LEQ("<="),EQ("="),GE(">"),GEQ(">")
}