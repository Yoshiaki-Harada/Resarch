fun sigma(values: List<Double>, decsion: String) {
    val str = values.mapIndexed { i, v ->
        "+ $v$decsion"
    }.joinToString(" ")
    println(str)
}

fun main(args: Array<String>) {
    val values = listOf(1.0, 2.0, 3.0, 0.0)
    sigma(values, "x")
}