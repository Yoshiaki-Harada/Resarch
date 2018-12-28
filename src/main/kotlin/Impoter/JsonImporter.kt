package Impoter

import java.io.File

class JsonImporter(filename: String) {
    val file = File("$filename.json").absoluteFile

    fun getString(): String {
        return file.bufferedReader().use { it.readText() }
    }
}
