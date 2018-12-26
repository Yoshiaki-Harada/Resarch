import java.io.File


class JsonWriter(val filename: String) {
    val file = File(filename + ".json").absoluteFile
    fun makeFile(json: String) {
        file.appendText(json)
    }
}