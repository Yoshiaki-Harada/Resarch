import ilog.cplex.IloCplex
import java.io.BufferedReader
import java.io.File


class JsonImporter(val filename: String) {
    val file = File(filename + ".json").absoluteFile

    fun getString(): String {
       return file.bufferedReader().use { it.readText() }
    }
}
