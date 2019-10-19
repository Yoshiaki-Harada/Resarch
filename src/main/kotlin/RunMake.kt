fun sigma(values: List<Double>, decsion: String) {
    val str = values.mapIndexed { i, v ->
        "+ $v$decsion"
    }.joinToString(" ")
    println(str)
}

fun main(args: Array<String>) {
    val values = listOf(0.0)
    values.filter { it < -1.0 }.average().nanTo0().let {
        print(it)
    }

}

inline fun Double.nanTo0(): Double {
    return if (this.isNaN()) 0.0
    else this
}