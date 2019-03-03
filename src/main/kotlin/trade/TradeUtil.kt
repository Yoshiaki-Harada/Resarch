package trade

import model.Bidder
import result.BidCal
import result.BidResult
import result.BidderCal

object TradeUtil {

    fun cost(x: List<List<List<DoubleArray>>>, providers: List<Bidder>, requesters: List<Bidder>): Double {
        var cost = 0.0
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jに提供するとき1となる変数
                        //provider_iがresource_rをrequester_jの入札の要求resource_mに提供する時間x(正の整数)
                        cost += resource.value.tValue * bid.bundle[r] * x[i][r][j][n]
                    }
                }
            }
        }
        return cost
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

    fun calProviderProfit(payment: Double, provider: Bidder, requester: Bidder, n: Int, r: Int): Double {
        //                                 cost                          timeRatio
        return payment - provider.bids[r].value.tValue * requester.bids[n].bundle[r]
    }

    fun calRequesterProfit(payment: Double, requester: Bidder, bidIndex: Int, resource: Int): Double {
        //     resourceに対する予算の密度                                                             timeRatio
        return calRequesterTrueBudgetDensity(requester, bidIndex, resource) * requester.bids[bidIndex].bundle[resource] - payment
    }

}

fun calRequesterTrueBudgetDensity(requester: Bidder, bidIndex: Int, resource: Int): Double {
    return (requester.bids[bidIndex].value.tValue * (requester.bids[bidIndex].bundle[resource] / requester.bids[bidIndex].bundle.sum())) / requester.bids[bidIndex].bundle[resource]
}


class ResultPre(
        val payments: MutableList<Double>,
        val providerCals: MutableList<BidderCal>,
        val requesterCals: MutableList<BidderCal>,
        val providerBidResults: MutableList<BidResult>,
        val requesterBidResults: MutableList<BidResult>) {

}