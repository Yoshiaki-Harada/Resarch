package trade.provider_single

import config.Config
import model.Bidder
import result.BidResult
import result.BidderCal
import trade.ResultPre
import trade.SingleSided
import trade.TradeUtil
import trade.average.AveTrade

class SingleTradeImpl(val providers: List<Bidder>, val requesters: List<Bidder>, val default: Config) : SingleSided {

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
    override fun run(x: List<List<List<DoubleArray>>>): ResultPre {
        var providerCals = mutableListOf<BidderCal>()
        var requesterCals = mutableListOf<BidderCal>()
        // 初期化
        TradeUtil.initBidderCals(providerCals, providers)
        TradeUtil.initBidderCals(requesterCals, requesters)
        val providerBidResults = mutableListOf<BidResult>()
        val requesterBidResults = mutableListOf<BidResult>()
        val payments = mutableListOf<Double>()
        val providerRewardsDensity = mutableListOf<Double>()


        //決定変数が1の時に取引を行う
        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        if (0.8 < d && d < 1.2) {
                            val payment = AveTrade(providers, requesters, default).payment(providers[i], requesters[j], n, r)
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

        providerCals.forEachIndexed { i, it ->
            val sumReward = it.bids.map { bid ->
                bid.payment
            }.sum()
            providerRewardsDensity.add(sumReward / providers[i].bids.map { bid -> bid.bundle.sum() }.sum())
        }

        return ResultPre(
                payments,
                providerCals,
                requesterCals,
                providerBidResults,
                requesterBidResults,
                payments,
                providerRewardsDensity
        )
    }
}
