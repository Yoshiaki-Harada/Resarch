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
import trade.initBidderCals
import trade.isOne
import winner.ProfitMaxDoubleAuction
import winner.ProfitMaxPaddingDoubleAuction

/**
 *
 * @property providers
 * @property requesters
 * @property default
 */
class VcgTrade(val providers: List<Bidder>, val requesters: List<Bidder>, val default: Config, val x: List<List<List<List<Double>>>>, val q: List<List<Double>>) {
    var providerCals = mutableListOf<BidderCal>()
    var requesterCals = mutableListOf<BidderCal>()
    val providerBidResults = mutableListOf<BidResult>()
    val requesterBidResults = mutableListOf<BidResult>()
    val payments = mutableListOf<Double>()
    val providerRevenues = mutableListOf<Double>()
    // [provider_1の提供リソース_1の単位あたりの報酬額,provider_1の提供リソース_2の単位あたりの報酬額,・・・・・]
    val providerRevenuesDensity = mutableListOf<Double>()

    // 初期化
    init {
        initBidderCals(providerCals, providers)
        initBidderCals(requesterCals, requesters)
    }

    fun run(y: List<List<Double>>, objValue: Double): ResultPre {
        requesterPayments(y, objValue)

        val winRequesters = getWinRequesters(y)
        val result = solveWinRequestersProblem(winRequesters = winRequesters)

        providerRewards(result.objValue, objValue, result.x, winRequesters)

        return ResultPre(
                payments,
                providerCals,
                requesterCals,
                providerBidResults,
                requesterBidResults,
                providerRevenues,
                providerRevenuesDensity,
                null
        )
    }

    private fun decideRequesterPayment(requesterId: Int, bidId: Int, excludedRequesters: List<Bidder>, objValue: Double): Double {
        // iを除いたオークションの目的関数の値を得る
        val conf = default.copy(lpFile = "Padding/auction-{$requesterId}")

        val bidders = providers.plus(excludedRequesters)
        ProfitMaxPaddingDoubleAuction(conf, Object.MAX, bidders).makeLpFile()
        val cplex = LpImporter("LP/Padding/auction-{$requesterId}").getCplex()
        cplex.solve()

        // 支払い価格を導出する
        println("${requesters[requesterId].bids[bidId].getValue()}, ${cplex.objValue} $objValue")
        val pay = requesters[requesterId].bids[bidId].getValue() - (objValue - cplex.objValue)
        println("requester$requesterId,$bidId payment = $pay")
        cplex.end()
        return pay
    }

    private fun getWinRequesters(y: List<List<Double>>): List<Bidder> {
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

    private fun solveWinRequestersProblem(winRequesters: List<Bidder>): Result {
        val conf = default.copy(lpFile = "Padding/winRequesters", provider = providers.size, requester = winRequesters.size)
        ProfitMaxDoubleAuction(conf, Object.MAX, providers.plus(winRequesters)).makeLpFile()
        val cplex = LpImporter("LP/Padding/winRequesters").getCplex()
        cplex.solve()
        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val cplexValue = cplex.getValues(lp)
        val tempY = cplexValue.copyOfRange(0, winRequesters.map { it.bids.size }.sum())
        val y = Util.convertDimension(tempY, winRequesters.map { it.bids.size })
        val excludedXCplex = cplexValue.copyOfRange(winRequesters.map { it.bids.size }.sum(), cplexValue.lastIndex + 1)
        println("excludedXCplexSize ${excludedXCplex.size}")
        val x = Util.convertDimension4(excludedXCplex, winRequesters.map { it.bids.size }, providers.map { it.bids.size }, conf)
        val objValue = cplex.objValue
        cplex.end()
        return Result(x, y, objValue)
    }

    data class Result(
            val x: List<List<List<DoubleArray>>>,
            val y: List<DoubleArray>,
            val objValue: Double
    )

    private fun requesterPayments(y: List<List<Double>>, objValue: Double) {
        y.forEachIndexed { j, bids ->
            bids.forEachIndexed { n, d ->
                if (isOne(d)) {
                    // 支払い価格を求める
                    val pay = decideRequesterPayment(j, n, excludeRequesters(requesters, j), objValue)
                    requesterCals[j].bids[n].addPayment(pay)
                    requesterCals[j].bids[n].addTime(requesters[j].bids[n].bundle.sum())
                    requesterCals[j].bids[n].addProfit(requesters[j].bids[n].value.tValue - pay)
                    requesterBidResults.add(BidResult(arrayOf(j, n), pay, requesters[j].bids[n].value.tValue - pay))
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
    private fun providerRewards(objValue: Double, paddingObjValue: Double, x: List<List<List<DoubleArray>>>, winRequesters: List<Bidder>) {
        x.forEachIndexed { i, provider ->
            var trade = 0
            provider.forEachIndexed { r, resource ->
                val time = resource.map { requester ->
                    if (requester.sum() > 0.1) trade += 1

                    requester.sum()
                }.sum()

                if (time > 0) {
                    // 前半のvcgプライス部分
                    val conf = default.copy(lpFile = "Padding/reqAuction-{$i}", provider = providers.size, requester = winRequesters.size)

                    ProfitMaxDoubleAuction(conf, Object.MAX, excludeProviders(providers, i, r).plus(winRequesters)).makeLpFile()

                    val cplex = LpImporter("LP/Padding/reqAuction-{$i}").getCplex()
                    cplex.solve()

                    println("quantity * providers[i].bids[r].getValue() + objValue - cplex.objValue = $time * ${providers[i].bids[r].getValue()} + $objValue -${cplex.objValue}")
                    val vcg = time * providers[i].bids[r].getValue() + objValue - cplex.objValue

                    //  後半のpayoff部分
                    val supremum = getSupremum(i, r, paddingObjValue)
                    val payoff = getPayOff(i, r, supremum, winRequesters, cplex.objValue)

                    // 収入の決定
                    val revenue = vcg - payoff

                    println("vcg - payoff = $vcg - $payoff")
                    println("provider_$i, resource_$r 's quantity = $time")
                    println("provider_$i, resource_$r 's cost = ${time * providers[i].bids[r].value.tValue}")
                    println("provider_$i, resource_$r 's revenue = $revenue")
                    println("provider_$i, resource_$r 's revenue / quantity= ${revenue / time}")

                    providerCals[i].bids[r].addPayment(revenue)
                    providerCals[i].bids[r].addTime(time)
                    providerCals[i].bids[r].addProfit(revenue - time * providers[i].bids[r].value.tValue)
                    providerBidResults.add(BidResult(arrayOf(i, r), revenue, revenue - time * providers[i].bids[r].value.tValue))
                    providerRevenues.add(revenue)
                    providerRevenuesDensity.add(revenue / time)
                    cplex.end()
                }
            }
        }
    }

    private fun getPayOff(id: Int, resoruceId: Int, supremum: Double, winRequesters: List<Bidder>, excludedObj: Double): Double {

        val newProviders = providers.map {
            if (it.id == id) {
                Bidder().add(it.bids.mapIndexed { index, bids ->
                    if (index == resoruceId) {
                        // 金額を整数値に限定するかは保留
                        println("supremum $supremum")
                        println("Ts ${bids.bundle[index]}")
                        println("p_$id(I,J,Q) = ${supremum / bids.bundle[index]}")
                        Bid(Value(supremum / bids.bundle[index], 0.0), bids.bundle)
                    } else {
                        bids
                    }
                })
            } else {
                it
            }
        }
        val conf1 = default.copy(provider = providers.size, requester = winRequesters.size, lpFile = "Padding/payoff-$id")

        ProfitMaxDoubleAuction(conf1, Object.MAX, newProviders.plus(winRequesters)).makeLpFile()
        val cplex = LpImporter("LP/Padding/payoff-$id").getCplex()
        cplex.solve()
        val objValue = cplex.objValue
        cplex.end()
        return objValue - excludedObj
    }

    private fun getSupremum(id: Int, resoruceId: Int, paddingObjValue: Double): Double {
        val conf = default.copy(provider = providers.size, requester = requesters.size, lpFile = "Padding/supremum-{$id}")
        ProfitMaxPaddingDoubleAuction(conf, Object.MAX, excludeProviders(providers, id, resoruceId).plus(requesters)).makeLpFile()
        val cplex = LpImporter("LP/Padding/supremum-{$id}").getCplex()
        cplex.solve()
        val time = x[id][resoruceId].map { it.sum() }.sum() + q[id][resoruceId]
        println("quqntity = $time")
        val objValue = cplex.objValue
        cplex.end()
        return providers[id].bids[resoruceId].getValue() * time + paddingObjValue - objValue
    }

    private fun excludeProviders(providers: List<Bidder>, providerId: Int, resourceId: Int) = providers.mapIndexed { pId, bidder ->
        if (providerId == pId) {
            val bids = bidder.bids.mapIndexed { rId, resource ->
                if (resourceId == rId) {
                    Bid(Value(0.0, 0.0), resource.bundle.map { 0.0 })
                } else {
                    resource
                }
            }
            Bidder().add(bids)
        } else {
            bidder
        }
    }

    private fun excludeRequesters(requesters: List<Bidder>, requesterId: Int) = requesters.mapIndexed { index, bidder ->
        if (bidder.id == requesterId) {
            val bids = bidder.bids.map {
                val bundle = it.bundle.map {
                    0.0
                }
                Bid(Value(0.0, 0.0), bundle)
            }
            Bidder().add(bids)
        } else {
            bidder
        }
    }
}