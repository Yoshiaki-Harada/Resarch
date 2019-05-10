fun main(args: Array<String>) {
    val t = "提供単価最小化-ペナルティ-10000.0-profit40%"
    t.replace(Regex("""""profit\d\d*%"""), "profit50*")
    println(t)
    println(t.replace(Regex("profit\\d*%"), "profit50"))

}