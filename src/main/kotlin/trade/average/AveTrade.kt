package trade.average

import config.Config
import model.Bidder
import result.BidResult
import result.BidderCal
import trade.DobuleSided
import trade.ResultPre
import trade.TradeUtil

class AveTrade(val providers: List<Bidder>, val requesters: List<Bidder>, val default: Config) : DobuleSided {

    // paymentを求める為の関数
    fun calRequesterBudgetDensity(requester: Bidder, bidIndex: Int, resource: Int): Double {
        return (requester.bids[bidIndex].getValue() * (requester.bids[bidIndex].bundle[resource] / requester.bids[bidIndex].bundle.sum())) / requester.bids[bidIndex].bundle[resource]
    }

    /**
     * 取引価格はお互いの希望の半分
     *
     * @param provider
     * @param requester
     * @param bidIndex
     * @param resource
     * @return
     */
    override fun payment(provider: Bidder, requester: Bidder, bidIndex: Int, resource: Int): Double {
        //resourceに対する予算の密度
        val budgetOfResource = calRequesterBudgetDensity(requester, bidIndex, resource)
        //提供側と要求側の予算密度の平均
        val avePay = (provider.bids[resource].getValue() + budgetOfResource) / 2
        //                                       timeRatio
        return avePay * requester.bids[bidIndex].bundle[resource]
    }

    /**
     * 取引価格はお互いの希望の半分
     *
     * @param x
     * @param providers
     * @param requesters
     * @return
     */
    override fun run(x: List<List<List<DoubleArray>>>): ResultPre {
        var providerCals = mutableListOf<BidderCal>()
        var requesterCals = mutableListOf<BidderCal>()
        // 初期化
        TradeUtil.initBidderCals(providerCals, providers)
        TradeUtil.initBidderCals(requesterCals, requesters)
        val providerBidResults = mutableListOf<BidResult>()
        val requesterBidResults = mutableListOf<BidResult>()
        val payments = mutableListOf<Double>()

        // 決定変数が1の時に取引を行う
        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        if (0.8 < d && d < 1.2) {
                            val payment = payment(providers[i], requesters[j], n, r)
                            payments.add(payment)
                            // 提供側
                            providerCals[i].bids[r].addPayment(payment)
                            providerCals[i].bids[r].addTime(requesters[j].bids[n].bundle[r])
                            providerCals[i].bids[r].addProfit(TradeUtil.calProviderProfit(payment, providers[i], requesters[j], n, r))
                            providerBidResults.add(BidResult(arrayOf(i, j, n, r), payment, TradeUtil.calProviderProfit(payment, providers[i], requesters[j], n, r)))
                            // 要求側
                            requesterCals[j].bids[n].addPayment(payment)
                            requesterCals[i].bids[n].addTime(requesters[j].bids[n].bundle[r])
                            requesterCals[j].bids[n].addProfit(TradeUtil.calRequesterProfit(payment, requesters[j], n, r))
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
                requesterBidResults
        )
    }
}
