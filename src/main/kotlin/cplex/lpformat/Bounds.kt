package cplex.lpformat

//各変数に対する制約（何もつけなければ非負変数）
enum class Bounds (val str: String){
    FREE("free"),LE("<"), LEQ("<="),GE(">"),GEQ(">")
}