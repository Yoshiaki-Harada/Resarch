package writer

import cplex.lpformat.Calculation
import cplex.lpformat.Constrait
import cplex.lpformat.Object
import cplex.lpformat.VarType
import java.io.BufferedWriter
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

    fun getBufferedWriter() = file.bufferedWriter()

    fun obj(obj: Object) {
        //出来れば一回しか書き込めないエラー処理
        file.appendText(obj.str + "\n")
    }

    fun obj(obj: Object, writer: BufferedWriter) {
        //出来れば一回しか書き込めないエラー処理
        writer.write(obj.str + "\n")
    }

    fun subto() {
        if (isNotContain("subject to"))
            file.appendText("subject to" + "\n")
    }

    fun subto(writer: BufferedWriter) {
        if (isNotContain("subject to"))
            writer.write("subject to" + "\n")
    }

    fun constrait(cons: Constrait) {
        file.appendText(cons.str + " ")
    }

    fun constrait(cons: Constrait, writer: BufferedWriter) {
        writer.write(cons.str + " ")
    }

    fun constrateName(name: String) {
        file.appendText("$name: ")
    }

    fun constrateName(name: String, writer: BufferedWriter) {
        writer.write("$name: ")
    }

    fun varType(varTypeType: VarType) {
        file.appendText(varTypeType.str + "\n")
    }

    fun varType(varTypeType: VarType, writer: BufferedWriter) {
        writer.write(varTypeType.str + "\n")
    }

    fun end() {
        file.appendText("end")
    }

    fun end(writer: BufferedWriter) {
        writer.write("end")
    }


    fun term(double: Double, variable: String, suffix: String) {
        when {
            double >= 0 -> file.appendText(" + $double $variable$suffix ")
            double < 0 -> file.appendText(" $double $variable$suffix ")
        }
    }

    fun term(double: Double, variable: String, suffix: String, writer: BufferedWriter) {
        when {
            double >= 0 -> writer.write(" + $double $variable$suffix ")
            double < 0 -> writer.write(" $double $variable$suffix ")
        }
    }

    fun minus(double: Double, variable: String, suffix: String) {
        file.appendText(" - $double $variable$suffix ")
    }

    fun minus(double: Double, variable: String, suffix: String, writer: BufferedWriter) {
        writer.write(" - $double $variable$suffix ")
    }

    fun number(double: Double) {
        when {
            double >= 0 -> file.appendText(" +$double ")
            double < 0 -> file.appendText(" $double ")
        }
    }

    fun number(double: Double, writer: BufferedWriter) {
        when {
            double >= 0 -> writer.write(" +$double ")
            double < 0 -> writer.write(" $double ")
        }
    }

    fun term(variable: String, suffix: String) {
        file.appendText(" +$variable$suffix ")
    }

    fun term(variable: String, suffix: String, writer: BufferedWriter) {
        writer.write(" +$variable$suffix ")
    }


    fun variable(variable: String, suffix: String) {
        file.appendText(" $variable$suffix ")
    }

    fun variable(variable: String, suffix: String, writer: BufferedWriter) {
        writer.write(" $variable$suffix ")
    }

    fun minus() {
        file.appendText(" " + Calculation.MINUS.str + " ")
    }

    fun minus(writer: BufferedWriter) {
        writer.write(" " + Calculation.MINUS.str + " ")
    }

    fun plus() {
        file.appendText(" " + Calculation.PLUS.str + " ")
    }

    fun plus(writer: BufferedWriter) {
        writer.write(" " + Calculation.PLUS.str + " ")
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

    fun arrow(writer: BufferedWriter) {
        writer.write("->")
    }

    fun newline() {
        file.appendText("\n")
    }

    fun newline(writer: BufferedWriter) {
        writer.write("\n")
    }
}