package trade.provider_single

import model.Bidder
import result.BidResult
import result.BidderCal
import trade.ResultPre
import trade.SingleSided
import trade.TradeUtil
import trade.average.AveTrade

object SingleTradeImpl : SingleSided {

    /**
     * 取引価格は，提供側の希望価格
     *
     * @param provider
     * @param requester
     * @param bidIndex
     * @param resource
     * @return
     */
    override fun payment(provider: Bidder, requester: Bidder, bidIndex: Int, resource: Int): Double {
        return provider.bids[resource].getValue() * requester.bids[bidIndex].bundle[resource]
    }

    /**
     * 取引価格は，提供側の希望価格
     *
     * @param x
     * @param providers
     * @param requesters
     * @return
     */
    override fun run(x: List<List<List<DoubleArray>>>, providers: List<Bidder>, requesters: List<Bidder>): ResultPre {
        var providerCals = mutableListOf<BidderCal>()
        var requesterCals = mutableListOf<BidderCal>()
        // 初期化
        TradeUtil.initBidderCals(providerCals, providers)
        TradeUtil.initBidderCals(requesterCals, requesters)
        val providerBidResults = mutableListOf<BidResult>()
        val requesterBidResults = mutableListOf<BidResult>()
        val payments = mutableListOf<Double>()

        //決定変数が1の時に取引を行う
        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        if (d == 1.0) {
                            val payment = AveTrade.payment(providers[i], requesters[j], n, r)
                            payments.add(payment)
                            // 提供側
                            providerCals[i].bids[r].addPayment(payment)
                            providerCals[i].bids[r].addProfit(TradeUtil.calProviderProfit(payment, providers[i], requesters[j], n, r))
                            providerCals[i].bids[r].addTime(requesters[j].bids[n].bundle[r])
                            providerBidResults.add(BidResult(arrayOf(i, j, n, r), payment, TradeUtil.calProviderProfit(payment, providers[i], requesters[j], n, r)))
                            // 要求側
                            requesterCals[j].bids[n].addPayment(payment)
                            requesterCals[j].bids[n].addProfit(TradeUtil.calRequesterProfit(payment, requesters[j], n, r))
                            requesterCals[i].bids[n].addTime(requesters[j].bids[n].bundle[r])
                            requesterBidResults.add(BidResult(arrayOf(i, j, n, r), payment, TradeUtil.calRequesterProfit(payment, requesters[j], n, r)))
                        }
                    }
                }
            }
        }

        return ResultPre(
                payments,
                providerCals,
                requesterCals,
                providerBidResults,
                requesterBidResults)
    }
}
