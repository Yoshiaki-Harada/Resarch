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
            run(it, config.resultDir, config.bidDir, dataset, config.targetDataIterate)
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
fun run(auction: String, resultDir: String, bidDir: String, dataSet: String, ite: Int) {
    val dirName = "${resultDir}${bidDir.replace("Bid", "")}/$dataSet"

    val rs = (0 until ite).toList().map {
        ResultConverter.fromJson(JsonImporter("$dirName/$it/$auction/result").getString())
    }


    val con = Conclusion(
            sumProfitAve = rs.map { it.sumProfit }.average(),
            sumProfitSD = rs.map { it.sumProfit }.sd(),
            providerProfitAve = rs.map { it.providerProfitAve }.average(),
            providerProfitSD = rs.map { it.providerProfitAve }.sd(),
            requesterProfitAve = rs.map { it.requesterProfitAve }.average(),
            requesterProfitSD = rs.map { it.requesterProfitAve }.sd(),
            winBidAve = rs.map { it.winBidRatio }.average(),
            winBidSD = rs.map { it.winBidRatio.toDouble() }.sd(),
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
                    providersProfitAve = rs.map { it.providerLiarsResult?.providerProfitAve ?: 0.0 }.average(),
                    providersProfitSD = rs.map { it.providerLiarsResult?.providerProfitAve ?: 0.0 }.sd(),
                    providersRevenueDensityAve = rs.map {
                        it.providerLiarsResult?.providerRevenueDensityAve ?: 0.0
                    }.average(),
                    providersRevenueDensitySD = rs.map {
                        it.providerLiarsResult?.providerRevenueDensityAve ?: 0.0
                    }.sd(),
                    providerProfitAve = rs.map { it.providerLiarResult.profit }.average(),
                    providerProfitSD = rs.map { it.providerLiarResult.profit }.sd(),
                    requestersProfitAve = rs.map { it.requesterLiarsResult?.requesterProfitAve ?: 0.0 }.average(),
                    requestersProfitSD = rs.map { it.requesterLiarsResult?.requesterProfitAve ?: 0.0 }.sd(),
                    requesterProfitAve = rs.map { it.requesterLiarResult.profit }.average(),
                    requesterProfitSD = rs.map { it.requesterLiarResult.profit }.sd()
            ),
            auctioneerProfitAve = rs.map { it.auctioneerProfit }.average(),
            auctioneerProfitSD = rs.map { it.auctioneerProfit }.sd(),
            sumRequesterProfitAve = rs.map { it.sumRequesterProfit }.average(),
            sumRequesterProfitSD = rs.map { it.sumRequesterProfit }.sd(),
            sumProviderProfitAve = rs.map { it.sumProviderProfit }.average(),
            sumProviderProfitSD = rs.map { it.sumProviderProfit }.sd(),
            surplusProfitAve = rs.map { it.auctioneerProfit }.average(),
            surplusProfitSD = rs.map { it.auctioneerProfit }.sd()
    )
    println("$dirName/$auction")
    writer.JsonWriter("$dirName/$auction").makeFile(ConclusionConverter.toJson(con))
}

