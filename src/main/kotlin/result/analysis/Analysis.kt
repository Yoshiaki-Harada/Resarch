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
    val config = Config.fromJson("config-lie")
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
            sumCostAve = rs.map { it.sumCost }.average(),
            sumCostSD = rs.map { it.sumCost }.sd(),
            sumProfitAve = rs.map { it.sumProfit }.average(),
            sumProfitSD = rs.map { it.sumProfit }.sd(),
            providerProfitAve = rs.map { it.providerProfitAve }.average(),
            providerProfitSD = rs.map { it.providerProfitAve }.sd(),
            requesterProfitAve = rs.map { it.requesterProfitAve }.average(),
            requesterProfitSD = rs.map { it.requesterProfitAve }.sd(),
            winBidAve = rs.map { it.winBidNUmber }.average(),
            winBidSD = rs.map { it.winBidNUmber.toDouble() }.sd(),
            providerTimeRatioAve = rs.map { it.providerTimeRatioAve }.average(),
            providerTimeRatioSD = rs.map { it.providerTimeRatioAve }.sd(),
            providerBeforeAvailabilityRatioAve = rs.map { it.beforeProviderAvailabilityRatioAve }.average(),
            providerBeforeAvailabilityRatioSD = rs.map { it.beforeProviderAvailabilityRatioAve }.sd(),
            providerAfterAvailabilityRatioAve = rs.map { it.afterProviderAvailabilityRatioAve }.average(),
            providerAfterAvailabilityRatioSD = rs.map { it.afterProviderAvailabilityRatioAve }.sd(),
            requesterPayAve = rs.map { it.requesterPayAve }.average(),
            requesterPaySD = rs.map { it.requesterPayAve }.sd(),
            providerRevenueAve = rs.map { it.providerRevenueAve }.average(),
            providerRevenueSD = rs.map { it.providerRevenueAve }.sd(),
            providerRevenueDensityAve = rs.map { it.providerRevenueDensityAve }.average(),
            providerRevenueDensitySD = rs.map { it.providerRevenueDensityAve }.sd(),
            sumPayAve = rs.map { it.sumPay }.average(),
            sumPaySD = rs.map { it.sumPay }.sd(),
            sumRevenueAve = rs.map { it.sumRevenue }.average(),
            sumRevenueSD = rs.map { it.sumRevenue }.sd(),
            sumProfitIncludeAuctioneerAve = rs.map { it.sumProfit + it.auctioneerProfit }.average(),
            sumProfitIncludeAuctioneerSD = rs.map { it.sumProfit + it.auctioneerProfit }.sd(),
            liarConclusion = LiarConclusion(
                    providerProfitAve = rs.map { it.providerLiarResult?.providerProfitAve ?: 0.0 }.average(),
                    providerProfitSD = rs.map { it.providerLiarResult?.providerProfitAve ?: 0.0 }.sd(),
                    providerRevenueDensityAve = rs.map {
                        it.providerLiarResult?.providerRevenueDensityAve ?: 0.0
                    }.average(),
                    providerRevenueDensitySD = rs.map {
                        it.providerLiarResult?.providerRevenueDensityAve ?: 0.0
                    }.sd()

            ),
            auctioneerProfitAve = rs.map { it.auctioneerProfit }.average(),
            auctioneerProfitSD = rs.map { it.auctioneerProfit }.sd()
    )
    println("$dir/$auction")
    writer.JsonWriter("$resultDir/$dataSet/$auction").makeFile(ConclusionConverter.toJson(con))
}

