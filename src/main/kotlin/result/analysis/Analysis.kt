package result.analysis

import Impoter.JsonImporter
import converter.ConclusionConverter
import converter.ResultConverter
import sd

fun main(args: Array<String>) {
    var min = 50.0
    var max = 150.0

    for (i in 0 until 5) {
        run(min, max)
        min += 50.0
        max += 50.0
    }
}

fun run(min: Double, max: Double) {
    val dir = "Result/supply-$min-$max/"
    val auction = "提供単価最小化-ペナルティ-10000.0-利益率60%"
    val rs = (0..4).toList().map {
        ResultConverter.fromJson(JsonImporter("$dir/$it/$auction/result").getString())
    }

    val con = Conclusion(
            rs.map { it.sumCost }.average(),
            rs.map { it.sumCost }.sd(),
            rs.map { it.sumProfit }.average(),
            rs.map { it.sumProfit }.sd(),
            rs.map { it.providerProfitAve }.average(),
            rs.map { it.providerProfitAve }.sd(),
            rs.map { it.requesterProfitAve }.average(),
            rs.map { it.requesterProfitAve }.sd(),
            rs.map { it.winBidNUmber }.average(),
            rs.map { it.winBidNUmber.toDouble() }.sd(),
            rs.map { it.providerTimeRatioAve }.average(),
            rs.map { it.providerTimeRatioAve }.sd()
    )

    writer.JsonWriter("$dir/$auction").makeFile(ConclusionConverter.toJson(con))
}

fun run(min: Double, max: Double, auction: String) {
    val dir = "Result/supply-$min-$max/"
    val rs = (0..4).toList().map {
        ResultConverter.fromJson(JsonImporter("$dir/$it/$auction/result").getString())
    }
    val con = Conclusion(
            rs.map { it.sumCost }.average(),
            rs.map { it.sumCost }.sd(),
            rs.map { it.sumProfit }.average(),
            rs.map { it.sumProfit }.sd(),
            rs.map { it.providerProfitAve }.average(),
            rs.map { it.providerProfitAve }.sd(),
            rs.map { it.requesterProfitAve }.average(),
            rs.map { it.requesterProfitAve }.sd(),
            rs.map { it.winBidNUmber }.average(),
            rs.map { it.winBidNUmber.toDouble() }.sd(),
            rs.map { it.providerTimeRatioAve }.average(),
            rs.map { it.providerTimeRatioAve }.sd()
    )
    println("$dir/$auction")
    writer.JsonWriter("$dir/$auction").makeFile(ConclusionConverter.toJson(con))
}
