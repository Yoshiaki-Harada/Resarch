package trade.padding_method

import model.Bidder
import result.BidResult
import result.BidderCal
import trade.ResultPre
import trade.TradeUtil
import trade.average.AveTrade


fun isOne(d: Double): Boolean {
    return (0.9 < d && d < 1.1)
}

class VcgTrade {
    fun run(x: List<List<List<DoubleArray>>>, providers: List<Bidder>, requesters: List<Bidder>): ResultPre {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun buyerPayment(x: List<List<List<DoubleArray>>>, providers: List<Bidder>, requesters: List<Bidder>): Double {
        var providerCals = mutableListOf<BidderCal>()
        // 初期化
        TradeUtil.initBidderCals(providerCals, providers)
        val providerBidResults = mutableListOf<BidResult>()
        val payments = mutableListOf<Double>()

        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        if (isOne(d)) {
                            val payment = AveTrade.payment(providers[i], requesters[j], n, r)
                            payments.add(payment)
                            // 提供側
                            providerCals[i].bids[r].addPayment(payment)
                            providerCals[i].bids[r].addTime(requesters[j].bids[n].bundle[r])
                            providerCals[i].bids[r].addProfit(TradeUtil.calProviderProfit(payment, providers[i], requesters[j], n, r))
                            providerBidResults.add(BidResult(arrayOf(i, j, n, r), payment, TradeUtil.calProviderProfit(payment, providers[i], requesters[j], n, r)))
                        }
                    }
                }
            }
        }
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun sellerPayment(provider: Bidder, requester: Bidder, bidIndex: Int, resource: Int): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}