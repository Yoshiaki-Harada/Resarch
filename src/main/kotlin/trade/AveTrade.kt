package trade

import model.Bidder
import result.BidResult
import result.BidderCal

// Ruleを継承したAveTrade(paymentとrunを継承?)
object AveTrade {
    fun calRequesterBudgetDensity(requester: Bidder, bidIndex: Int, resource: Int): Double {
        return (requester.bids[bidIndex].getValue() * (requester.bids[bidIndex].bundle[resource] / requester.bids[bidIndex].bundle.sum())) / requester.bids[bidIndex].bundle[resource]
    }

    fun payment(provider: Bidder, requester: Bidder, bidIndex: Int, resource: Int): Double {
        //resourceに対する予算の密度
        val budgetOfResource = AveTrade.calRequesterBudgetDensity(requester, bidIndex, resource)
        //提供側と要求側の予算密度の平均
        val avePay = (provider.bids[resource].getValue() + budgetOfResource) / 2
        //                                       time
        return avePay * requester.bids[bidIndex].bundle[resource]
    }

    //　Calsを引数にせずに本来は計算結果を生成して返す?
    fun run(x: List<List<List<DoubleArray>>>, providers: List<Bidder>, requesters: List<Bidder>, payments: MutableList<Double>, providerCals: MutableList<BidderCal>, providerBidResults: MutableList<BidResult>, requesterCals: MutableList<BidderCal>, requesterBidResults: MutableList<BidResult>) {
        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        if (d == 1.0) {
                            val payment = AveTrade.payment(providers[i], requesters[j], n, r)
                            payments.add(payment)
                            //提供側
                            providerCals[i].bids[r].addPayment(payment)
                            providerCals[i].bids[r].addProfit(TradeUtil.providerProfit(payment, providers[i], requesters[j], n, r))
                            providerBidResults.add(BidResult(arrayOf(i, j, n, r), payment, TradeUtil.providerProfit(payment, providers[i], requesters[j], n, r)))
                            //要求側
                            requesterCals[j].bids[n].addPayment(payment)
                            requesterCals[j].bids[n].addProfit(TradeUtil.requesterProfit(payment, requesters[j], n, r))
                            requesterBidResults.add(BidResult(arrayOf(i, j, n, r), payment, TradeUtil.requesterProfit(payment, requesters[j], n, r)))
                        }
                    }
                }
            }
        }
    }
}