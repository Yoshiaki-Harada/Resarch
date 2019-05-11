package writer

import cplex.lpformat.*
import java.io.File

/**
 * LPファイルを作成する為のクラス
 */
class LpWriter(val filename: String) {

    val file = File("$filename.lp").absoluteFile

    init {
        if (file.delete())
            println("$filename を削除しました")
    }

    fun obj(obj: Object) {
        //出来れば一回しか書き込めないエラー処理
        file.appendText(obj.str + "\n")
    }

    fun subto() {
        if (isNotContain("subject to"))
            file.appendText("subject to" + "\n")
    }

    fun constrait(cons: Constrait) {
        file.appendText(cons.str + " ")
    }

    fun constrateName(name: String) {
        file.appendText("$name: ")
    }

    fun bounds(bounds: Bounds) {
        //文字列"bounds"がなければ追記できるような場合分けを書きたい
        if (isNotContain("bounds"))
            file.appendText("bounds" + "\n")
        file.appendText(bounds.str + "\n")
    }

    fun varType(varTypeType: VarType) {
        file.appendText(varTypeType.str + "\n")
    }

    fun end() {
        file.appendText("end")
    }

    fun leftBracket() {
        file.appendText("(")
    }

    fun rightBracket() {
        file.appendText(")")
    }

    fun term(double: Double, variable: String, suffix: String) {
        when {
            double >= 0 -> file.appendText(" + $double $variable$suffix ")
            double < 0 -> file.appendText(" $double $variable$suffix ")
        }
    }

    fun minus(double: Double, variable: String, suffix: String) {
        file.appendText(" - $double $variable$suffix ")
    }

    fun number(double: Double) {
        when {
            double >= 0 -> file.appendText(" +$double ")
            double < 0 -> file.appendText(" $double ")
        }
    }

    fun pureNumber(double: Double) {
        file.appendText(" $double ")
    }

    fun term(variable: String, suffix: String) {
        file.appendText(" +$variable$suffix ")
    }


    fun variable(variable: String, suffix: String) {
        file.appendText(" $variable$suffix ")
    }

    fun minus() {
        file.appendText(" " + Calculation.MINUS.str + " ")
    }

    fun plus() {
        file.appendText(" " + Calculation.PLUS.str + " ")
    }

    fun mul(variable: String, suffix: String) {
        file.appendText(Calculation.MULTI.str + " $variable$suffix ")
    }


    fun mul(double: Double, variable1: String, suffix1: String, variable2: String, suffix2: String) {
        file.appendText("+ " + double.toString() + " $variable1$suffix1 " + Calculation.MULTI.str + " $variable2$suffix2 ")
    }

    fun mul(variable1: String, suffix1: String, variable2: String, suffix2: String) {
        file.appendText("+ [ $variable1$suffix1 " + Calculation.MULTI.str + " $variable2$suffix2 ] ")
    }

    fun isNotContain(str: String): Boolean {
        val regex = Regex(str)
        val lines = file.reader().useLines {
            it.filter(String::isNotBlank).toList()
        }

        return lines.filter {
            regex.containsMatchIn(it)
        }.isEmpty()
    }

    fun arrow() {
        file.appendText("->")
    }

    fun newline() {
        file.appendText("\n")
    }

}