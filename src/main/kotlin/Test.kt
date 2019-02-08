import config.Config
import java.util.*
import kotlin.streams.toList

fun main(args: Array<String>) {

    val rands = (1..4).toList().shuffled()
    val resource = rands.subList(0, 2).sorted()
    println(rands)
    println(resource)

}