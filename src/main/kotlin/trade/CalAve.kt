package trade

import model.Bidder
import result.BidCal
import result.BidderCal

object CalAve {
    fun calRequesterBudgetDensity(requester: Bidder, bidIndex: Int, resource: Int): Double {
        return (requester.bids[bidIndex].getValue() * (requester.bids[bidIndex].bundle[resource] / requester.bids[bidIndex].bundle.sum())) / requester.bids[bidIndex].bundle[resource]
    }

    fun payment(provider: Bidder, requester: Bidder, bidIndex: Int, resource: Int): Double {
        //resourceに対する予算の密度
        val budgetOfResource = CalAve.calRequesterBudgetDensity(requester, bidIndex, resource)
        //提供側と要求側の予算密度の平均
        val avePay = (provider.bids[resource].getValue() + budgetOfResource) / 2
        //                                       time
        return avePay * requester.bids[bidIndex].bundle[resource]
    }
}