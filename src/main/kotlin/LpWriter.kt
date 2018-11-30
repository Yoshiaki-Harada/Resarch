import lpformat.*
import java.io.File

class LpWriter(val filename: String) {

    val file = File(filename).absoluteFile

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

    fun constrateName(name :String){
        file.appendText(name+": ")
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

    fun term(double :Double, variable :String, suffix :String){
        file.appendText(" " + double.toString() + variable + suffix)
    }

    fun number(double :Double){
        file.appendText(" " + double.toString()+ " ")
    }


    fun term(variable :String, suffix :String){
        file.appendText(" $variable$suffix ")
    }

    fun plus(){
        file.appendText(" " + Calculation.PLUS.str)
    }

    fun mul(){
        file.appendText(" " + Calculation.MULTI.str)
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

    fun newline(){
        file.appendText("\n")
    }

}