package config

import com.squareup.moshi.JsonWriter
import converter.ConfigConverter
import converter.JsonConverter

fun main(args: Array<String>) {
    val config = Config(
            0,
            0,
            "Bid/Bidder",
            "LP/lp_file",
            0,
            "result",
            "tmpFile"
    )
    writer.JsonWriter("config").makeFile(ConfigConverter.toJson(config))
}