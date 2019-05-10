package writer

import java.io.File

/**
 * jsonファイルを作成する為のクラス
 */
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