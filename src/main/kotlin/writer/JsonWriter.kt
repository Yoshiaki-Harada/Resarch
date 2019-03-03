package writer

import java.io.File


class JsonWriter(val filename: String) {

    val file = File("$filename.json").absoluteFile

    init {
        if (file.delete())
            println("$file を削除しました")
    }

    fun makeFile(json: String) {
        file.appendText(json)
    }

}