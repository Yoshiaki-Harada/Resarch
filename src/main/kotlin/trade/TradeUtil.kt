package trade

import model.Bidder

object TradeUtil {
    fun providerProfit(payment: Double, provider: Bidder, requester: Bidder, n: Int, r: Int): Double {
        //                                 cost                          time
        return payment - provider.bids[r].getValue() * requester.bids[n].bundle[r]
    }

    fun requesterProfit(payment: Double, requester: Bidder, bidIndex: Int, resource: Int): Double {
        //     resourceに対する予算の密度                                                             time
        return CalAve.calRequesterBudgetDensity(requester, bidIndex, resource) * requester.bids[bidIndex].bundle[resource] - payment
    }
}