package config

import converter.ConfigConverter

fun main(args: Array<String>) {
    val config = Config(
            8,
            500.0,
            1000.0,
            0.1,
            0.5,
            1,
            4,
            0.0,
            100.0,
            1.0,
            2.0,
            1,
            2,
            1000.0,
            "Bid/First/bidder",
            "Bid/First/bidder",
            "LP/lp_file",
            "",
            0,
            "test_result_file",
            "test_result_file",
            10,
            1000.0,
            listOf(),
            listOf(),
            0,
            emptyList<String>(),
            1

    )
    writer.JsonWriter("config").makeFile(ConfigConverter.toJson(config))
}