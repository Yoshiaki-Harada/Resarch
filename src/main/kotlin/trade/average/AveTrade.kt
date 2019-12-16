package trade.average

import config.Config
import model.Bidder
import result.BidResult
import result.BidderCal
import trade.DobuleSided
import trade.ResultPre
import trade.TradeUtil

class AveTrade(val providers: List<Bidder>, val requesters: List<Bidder>, val default: Config) : DobuleSided {
    private val brokerageRate = 0.0
    /**
     * 取引価格はお互いの希望の半分
     *
     * @param provider
     * @param requester
     * @param bidIndex
     * @param resource
     * @param provideTs
     * @return
     */
    override fun payment(provider: Bidder, requester: Bidder, bidIndex: Int, resource: Int, provideTs: Double): Double {
        val requesterValue = (provideTs / requester.bids[bidIndex].bundle.sum()) * requester.bids[bidIndex].getValue()
        val providerValue = provider.bids[resource].getValue() * provideTs
        return (requesterValue + providerValue) / 2
    }

    /**
     * 取引価格はお互いの希望の半分
     *
     * @param x
     * @param providers
     * @param requesters
     * @return
     */
    override fun run(x: List<List<List<List<Double>>>>): ResultPre {
        val providerCals = mutableListOf<BidderCal>()
        val requesterCals = mutableListOf<BidderCal>()
        val providerRevenueDensity = mutableListOf<Double>()

        // 初期化
        TradeUtil.initBidderCals(providerCals, providers)
        TradeUtil.initBidderCals(requesterCals, requesters)
        val providerBidResults = mutableListOf<BidResult>()
        val requesterBidResults = mutableListOf<BidResult>()
        val payments = mutableListOf<Double>()
        // 決定変数が正の時
        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        if (d > 0.01) {
                            val payment = payment(providers[i], requesters[j], n, r, d)
                            payments.add(payment)
                            // 提供側
                            providerCals[i].bids[r].addPayment(payment)
                            providerCals[i].bids[r].addTime(d)
                            providerCals[i].bids[r].addProfit(payment - d * providers[i].bids[r].value.tValue)
                            providerBidResults.add(BidResult(arrayOf(i, j, n, r), payment, payment - d * providers[i].bids[r].value.tValue))
                            // 要求側
                            requesterCals[j].bids[n].addPayment(payment)
                            requesterCals[j].bids[n].addTime(requesters[j].bids[n].bundle[r])
                            val ratio = d / requesters[j].bids[n].bundle.sum()
                            requesterCals[j].bids[n].addProfit(ratio * requesters[j].bids[n].value.tValue - payment)
                            requesterBidResults.add(BidResult(arrayOf(i, j, n, r), payment, ratio * requesters[j].bids[n].value.tValue * payment))
                        }
                    }
                }
            }
        }

        providerCals.forEachIndexed { i, it ->
            val sumReward = it.bids.map { bid ->
                bid.payment
            }.sum()
            if (sumReward != 0.0) {
                providerRevenueDensity.add(sumReward / x[i].map { r -> r.map { j -> j.sum() }.sum() }.sum())
            }
        }

        // 主催者の利益の計算
        var auctioneerProfit = 0.0
        providerCals.forEachIndexed { i, it ->
            it.bids.forEach {
                val p = it.payment * brokerageRate
                it.payment = it.payment * (1.0 + brokerageRate)
                it.profit = it.profit - p
                auctioneerProfit += p
            }
        }
        requesterCals.forEach {
            it.bids.map { b ->
                val p = b.payment * brokerageRate
                b.payment = b.payment * (1.0 - brokerageRate)
                b.profit = b.profit - p
                auctioneerProfit += p
            }
        }

        return ResultPre(
                payments,
                providerCals,
                requesterCals,
                providerBidResults,
                requesterBidResults,
                payments,
                providerRevenueDensity,
                auctioneerProfit
        )
    }
}
