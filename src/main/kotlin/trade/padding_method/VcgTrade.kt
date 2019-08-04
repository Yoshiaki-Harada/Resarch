package trade.padding_method

import config.Config
import cplex.lpformat.Object
import impoter.LpImporter
import model.Bidder
import result.BidResult
import result.BidderCal
import trade.ResultPre
import trade.TradeUtil
import winner.ProfitMaxPaddingDoubleAuction


fun isOne(d: Double): Boolean {
    return (0.9 < d && d < 1.1)
}

class VcgTrade(val providers: List<Bidder>, val requesters: List<Bidder>) {
    var providerCals = mutableListOf<BidderCal>()
    var requesterCals = mutableListOf<BidderCal>()
    // 初期化
    val providerBidResults = mutableListOf<BidResult>()
    val requesterBidResults = mutableListOf<BidResult>()
    val payments = mutableListOf<Double>()

    init {
        TradeUtil.initBidderCals(providerCals, providers)
        TradeUtil.initBidderCals(requesterCals, requesters)
    }

    fun run(x: List<List<List<DoubleArray>>>): ResultPre {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun decideProviderReward() {

    }

    fun decideRequesterPayment(requesterId: Int, bidId: Int, excludedRequesters: List<Bidder>, objValue: Double, config: Config): Double {
        // iを除いたオークションの目的関数の値を得る
        println("decide $requesterId 's payment")

        config.lpFile = "Padding/auction\\{$requesterId}"
        config.requester = config.requester - 1

        val bidders = providers.plus(excludedRequesters)
        ProfitMaxPaddingDoubleAuction.makeLpFile(config, Object.MAX, bidders)
        val cplex = LpImporter("LP/Padding/auction\\{$requesterId}").getCplex()
        cplex.solve()

        // 支払い価格を導出する
        println("${requesters[requesterId].bids[bidId].getValue()}, ${cplex.objValue} $objValue")
        val pay = requesters[requesterId].bids[bidId].getValue() - (objValue - cplex.objValue)
        println("requester$requesterId,$bidId payment = $pay")

        return pay
    }

    fun buyerPayments(y: List<DoubleArray>, objValue: Double, config: config.Config) {
        y.forEachIndexed { j, bids ->
            bids.forEachIndexed { n, d ->
                if (isOne(d)) {
                    // 支払い価格を求める
                    requesters.filter { requester ->
                        requester.id != j
                    }.let { excludedRequesters ->
                        val pay = decideRequesterPayment(j, n, excludedRequesters, objValue, config)
                        requesterCals[j].bids[n].addPayment(pay)
                        requesterCals[j].bids[n].addTime(requesters[j].bids[n].bundle.sum())
                        requesterCals[j].bids[n].addProfit(requesters[j].bids[n].getValue() - pay)
                    }
                }
            }
        }
    }

    fun sellerPayment(provider: Bidder, requester: Bidder, bidIndex: Int, resource: Int): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}