package trade.padding_method

import Util
import config.Config
import cplex.lpformat.Object
import ilog.concert.IloLPMatrix
import impoter.LpImporter
import model.Bid
import model.Bidder
import model.Value
import result.BidResult
import result.BidderCal
import trade.ResultPre
import trade.TradeUtil
import trade.isOne
import winner.ProfitMaxDoubleAuction
import winner.ProfitMaxPaddingDoubleAuction


class VcgTrade(val providers: List<Bidder>, val requesters: List<Bidder>) {
    var providerCals = mutableListOf<BidderCal>()
    var requesterCals = mutableListOf<BidderCal>()
    val providerBidResults = mutableListOf<BidResult>()
    val requesterBidResults = mutableListOf<BidResult>()
    val payments = mutableListOf<Double>()

    // 初期化
    init {
        TradeUtil.initBidderCals(providerCals, providers)
        TradeUtil.initBidderCals(requesterCals, requesters)
    }

    private fun getWinRequesters(y: List<DoubleArray>): List<Bidder> {
        val wins = mutableListOf<Bidder>()
        y.forEachIndexed { j, bids ->
            bids.forEachIndexed { n, d ->
                if (isOne(d)) {
                    wins.add(Bidder(requesters[j].bids[n]))
                }
            }
        }
        val winRequesters = wins as List<Bidder>

        return winRequesters
    }

    private fun solveWinRequestersProblem(winRequesters: List<Bidder>, config: Config): Result {

        config.lpFile = "Padding/winRequester"

        config.provider = providers.size
        config.requester = winRequesters.size

        ProfitMaxDoubleAuction.makeLpFile(config, Object.MAX, providers.plus(winRequesters))
        val cplex = LpImporter("LP/Padding/winRequeste").getCplex()

        cplex.solve()

        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val cplexValue = cplex.getValues(lp)
        val tempY = cplexValue.copyOfRange(0, winRequesters.map { it.bids.size }.sum())
        val y = Util.convertDimension(tempY, requesters.map { it.bids.size })
        val excludedXCplex = cplexValue.copyOfRange(winRequesters.map { it.bids.size }.sum(), cplexValue.lastIndex - config.resource + 1)
        val x = Util.convertDimension4(excludedXCplex, winRequesters.map { it.bids.size }, providers.map { it.bids.size }, config)

        return Result(x, y, cplex.objValue)
    }

    data class Result(
            val x: List<List<List<DoubleArray>>>,
            val y: List<DoubleArray>,
            val objValue: Double
    )

    fun run(y: List<DoubleArray>, objValue: Double, config: Config): ResultPre {
        requesterPayments(y, objValue, config)

        val winRequesters = getWinRequesters(y)
        val result = solveWinRequestersProblem(winRequesters = winRequesters, config = config)

        providerRewards(result.objValue, objValue, result.x, winRequesters, config)

        return ResultPre(
                payments,
                providerCals,
                requesterCals,
                providerBidResults,
                requesterBidResults
        )
    }


    private fun decideRequesterPayment(requesterId: Int, bidId: Int, excludedRequesters: List<Bidder>, objValue: Double, config: Config): Double {
        // iを除いたオークションの目的関数の値を得る
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

    private fun requesterPayments(y: List<DoubleArray>, objValue: Double, config: Config) {
        y.forEachIndexed { j, bids ->
            bids.forEachIndexed { n, d ->
                if (isOne(d)) {
                    // 支払い価格を求める
                    val pay = decideRequesterPayment(j, n, requesters.filter { it.id != j }, objValue, config)
                    requesterCals[j].bids[n].addPayment(pay)
                    requesterCals[j].bids[n].addTime(requesters[j].bids[n].bundle.sum())
                    requesterCals[j].bids[n].addProfit(requesters[j].bids[n].value.tValue - pay)
                    providerBidResults.add(j, BidResult(arrayOf(j, n), pay, pay - requesters[j].bids[n].value.tValue))
                    payments.add(pay)
                }
            }
        }
    }

    /**
     *
     *
     * @param objValue
     * @param paddingObjValue
     * @param x (P(\tilde{I},J)の解)
     * @param winRequesters
     * @param config
     */
    private fun providerRewards(objValue: Double, paddingObjValue: Double, x: List<List<List<DoubleArray>>>, winRequesters: List<Bidder>, config: Config) {
        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                val quantity = resource.map { requester ->
                    requester.sum()
                }.sum()
                if (quantity > 0) {
                    // 前半のvcgプライス部分
                    config.lpFile = "Padding/reqAuction\\{$i}"
                    config.requester = provider.size - 1
                    ProfitMaxDoubleAuction.makeLpFile(config, Object.MAX, providers.filter { it.id != i }.plus(winRequesters))
                    val cplex = LpImporter("LP/Padding/reqAuction\\{$i}").getCplex()
                    cplex.solve()
                    val vcg = quantity * providers[i].bids[r].getValue() + objValue - cplex.objValue

                    //  後半のpayoff部分
                    val supremum = getSupremum(i, r, quantity, paddingObjValue, winRequesters, config)
                    val payoff = getPayOff(i, r, supremum, winRequesters, config)

                    // 報酬額を決定する
                    val reward = vcg - payoff

                    println("provider_$i, resource_$r 's quantity = $quantity")
                    println("provider_$i, resource_$r 's reward = $reward")

                    providerCals[i].bids[r].addPayment(reward)
                    providerCals[i].bids[r].addTime(quantity)
                    providerCals[i].bids[r].addProfit(quantity * providers[i].bids[r].value.tValue - reward)
                    requesterBidResults.add(i, BidResult(arrayOf(i, r), reward, quantity * providers[i].bids[r].value.tValue - reward))
                }
            }
        }
    }

    private fun getPayOff(id: Int, resoruceId: Int, supremum: Double, winRequesters: List<Bidder>, config: Config): Double {
        val newProviders = providers.map {
            if (it.id == id) {
                Bidder().add(it.bids.mapIndexed { index, bids ->
                    if (index == resoruceId) {
                        // 金額を整数値に限定するかは保留
                        println("p_$id(I,J,Q) = ${(supremum / bids.bundle[index]).toInt().toDouble()}")
                        Bid(Value((supremum / bids.bundle[index]).toInt().toDouble(), 0.0), bids.bundle)
                    } else {
                        bids
                    }
                })
            } else {
                it
            }
        }
        config.provider = newProviders.size
        config.requester = winRequesters.size
        config.lpFile = "Padding/payoff-$id"

        ProfitMaxDoubleAuction.makeLpFile(config, Object.MAX, newProviders.plus(winRequesters))
        val cplex = LpImporter("LP/Padding/payoff-$id").getCplex()
        cplex.solve()

        config.provider = newProviders.size
        config.requester = winRequesters.size
        config.lpFile = "Padding/payoff\\{$id}"
        ProfitMaxDoubleAuction.makeLpFile(config, Object.MAX, newProviders.filter { it.id != id }.plus(winRequesters))
        val excludedCplex = LpImporter("LP/Padding/payoff\\{$id}").getCplex()
        excludedCplex.solve()

        return cplex.objValue - excludedCplex.objValue
    }

    private fun getSupremum(id: Int, resoruce: Int, quantity: Double, paddingObjValue: Double, winRequesters: List<Bidder>, config: Config): Double {
        config.provider = providers.size - 1
        config.requester = winRequesters.size
        config.lpFile = "Padding/supremum\\{$id}"
        ProfitMaxPaddingDoubleAuction.makeLpFile(config, Object.MAX, providers.filter { it.id != id }.plus(winRequesters))
        val cplexVcg = LpImporter("Padding/supremum\\{$id}").getCplex()

        return providers[id].bids[resoruce].getValue() * quantity + paddingObjValue - cplexVcg.objValue
    }
}