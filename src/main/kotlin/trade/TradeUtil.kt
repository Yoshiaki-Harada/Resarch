package trade

import config.Config
import model.Bidder
import result.BidCal
import result.BidResult
import result.BidderCal
import result.ProviderResult


object TradeUtil {
    // 結果出力用リストの初期化
    fun initBidderCals(bidderCals: MutableList<BidderCal>, bidders: List<Bidder>) {
        println("bidders:" + bidders.size)
        bidders.forEach {
            val bidCal = BidderCal()
            it.bids.forEach {
                bidCal.bids.add(BidCal())
            }
            bidderCals.add(bidCal)
        }
    }
}

fun isOne(d: Double): Boolean {
    return (0.9 < d && d < 1.1)
}

fun cost(x: List<List<List<List<Double>>>>, providers: List<Bidder>, requesters: List<Bidder>): Double {
    var cost = 0.0
    providers.forEachIndexed { i, provider ->
        provider.bids.forEachIndexed { r, resource ->
            requesters.forEachIndexed { j, requester ->
                requester.bids.forEachIndexed { n, bid ->
                    //provider_iがresource_rをrequester_jの入札の要求resource_mに提供する時間x(正の整数)
                    cost += resource.value.tValue * x[i][r][j][n]
                }
            }
        }
    }
    return cost
}

/**
 * providerの結果を出力する関数
 *
 * @param provideTimes 各企業の提供可能時間のリスト
 * @param rs Resultの準備用クラス
 * @param config
 * @return providerResult
 */
fun calProviderResult(provideTimes: List<Double>, rs: ResultPre, config: Config): List<ProviderResult> {
    return rs.providerCals.mapIndexed { i, it ->
        // 1- 提供可能時間[Ts]/1期間[Ts]*提供リソース数 = 稼働率
        val beforeAvailabilityRatio = 1 - provideTimes[i].div(config.period.times(config.providerResourceNumber))
        // 稼働率 + 総提供時間 / 1期間[Ts]*提供リソース数
        val afterAvailabilityRatio = beforeAvailabilityRatio.plus(it.bids.map { it.time }.sum().div(config.period.times(config.providerResourceNumber)))
        ProviderResult(
                i,
                it.bids.map { it.payment }.sum(),
                it.bids.map { it.profit }.sum(),
                it.bids.map { it.time }.sum().div(provideTimes[i]),
                beforeAvailabilityRatio,
                afterAvailabilityRatio
        )
    }
}

// 結果出力用リストの初期化
fun initBidderCals(bidderCals: MutableList<BidderCal>, bidders: List<Bidder>) {
    println("bidders:" + bidders.size)
    bidders.forEach {
        val bidCal = BidderCal()
        it.bids.forEach {
            bidCal.bids.add(BidCal())
        }
        bidderCals.add(bidCal)
    }
}


data class ResultPre(
        val payments: MutableList<Double>,
        val providerCals: MutableList<BidderCal>,
        val requesterCals: MutableList<BidderCal>,
        val providerBidResults: MutableList<BidResult>,
        val requesterBidResults: MutableList<BidResult>,
        val providerRevenue: MutableList<Double>,
        val providerRevenueDensity: MutableList<Double>,
        val auctioneerProfit: Double?)

