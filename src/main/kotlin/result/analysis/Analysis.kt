package result.analysis

import config.Config
import converter.ConclusionConverter
import converter.ResultConverter
import impoter.JsonImporter
import sd

/**
 * 繰り返した結果の平均と標準偏差を求めるためのmain関数
 */
fun main(args: Array<String>) {
    val config = Config.fromJson("config")
    config.targetData.forEach { dataset ->
        config.targetAuction.forEach {
            if (config.targetDataIterate == null) {
                throw Exception("ite がnullです")
            }
            run(it, config.resultDir, dataset, config.targetDataIterate!!)
        }
    }
}


/**
 *  $resultDir/$dataset/$ite/$auction のresult.jsonの結果をまとめて，$resultDir/$datasetにauction名で保存
 *
 * @param auction
 * @param resultDir
 * @param dataSet
 * @param ite
 */
fun run(auction: String, resultDir: String, dataSet: String, ite: Int) {
    val dir = "$resultDir/$dataSet"

    val rs = (0 until ite).toList().map {
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
            rs.map { it.providerTimeRatioAve }.sd(),
            rs.map { it.tradeAve }.average(),
            rs.map { it.tradeAve }.sd(),
            rs.map { it.beforeProviderAvailabilityRatioAve }.average(),
            rs.map { it.beforeProviderAvailabilityRatioAve }.sd(),
            rs.map { it.afterProviderAvailabilityRatioAve }.average(),
            rs.map { it.afterProviderAvailabilityRatioAve }.sd()
    )
    println("$dir/$auction")
    writer.JsonWriter("$resultDir/$dataSet/$auction").makeFile(ConclusionConverter.toJson(con))
}

