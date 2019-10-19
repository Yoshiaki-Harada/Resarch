import config.Config
import converter.ConclusionConverter
import converter.ResultConverter
import impoter.JsonImporter
import result.analysis.Conclusion
import result.analysis.LiarConclusion

fun main() {
    val config = Config.fromJson("config")
    config.targetData.forEach { dataset ->
        if (config.targetDataIterate == null) {
            throw Exception("ite がnullです")
        }
        val dir = "${config.resultDir}/$dataset"

        val rs1 = (0 until config.targetDataIterate).toList().map {
            ResultConverter.fromJson(JsonImporter("$dir/$it/PaddingMethod/result").getString())
        }
        val rs2 = (0 until config.targetDataIterate).toList().map {
            ResultConverter.fromJson(JsonImporter("$dir/$it/利益最大化-平均/result").getString())
        }
        val decreaseRates = List(config.targetDataIterate) {
            val padding = rs1[it].sumProfit + rs1[it].sumPay - rs1[it].sumRevenue
            val ave = rs2[it].sumProfit
            padding / ave - 1.0
        }
        println("${decreaseRates.average()}     ${decreaseRates.sd()}")
    }
}

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
            sumProfitIncludeAuctioneerAve = rs.map { it.sumProfit + it.sumPay - it.sumRevenue }.average(),
            sumProfitIncludeAuctioneerSD = rs.map { it.sumProfit + it.sumPay - it.sumRevenue }.sd(),
            liarConclusion = LiarConclusion(
                    providerProfitAve = rs.map { it.liarResult?.providerProfitAve ?: 0.0 }.average(),
                    providerProfitSD = rs.map { it.liarResult?.providerProfitAve ?: 0.0 }.sd(),
                    providerRevenueDensityAve = rs.map {
                        it.liarResult?.providerRevenueDensityAve ?: 0.0
                    }.average(),
                    providerRevenueDensitySD = rs.map {
                        it.liarResult?.providerRevenueDensityAve ?: 0.0
                    }.sd()
            ),
            auctioneerProfitAve = 0.0,
            auctioneerProfitSD = 0.0
    )
    println("$dir/$auction")
    writer.JsonWriter("$resultDir/$dataSet/$auction").makeFile(ConclusionConverter.toJson(con))
}
