package config

import com.squareup.moshi.JsonWriter
import converter.ConfigConverter
import converter.JsonConverter

fun main(args: Array<String>) {
    val config = Config(
            1,
            500.0,
            1000.0,
            0.1,
            0.5,
            1,
            2,
            0.0,
            100.0,
            1.0,
            2.0,
            1,
            2,
            "Bid/Bidder",
            "LP/lp_file",
            0,
            "result_file",
            "tmp_file"
    )
    writer.JsonWriter("config").makeFile(ConfigConverter.toJson(config))
}