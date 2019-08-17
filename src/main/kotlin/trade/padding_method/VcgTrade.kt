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
import trade.nealyEqual
import winner.ProfitMaxDoubleAuction
import winner.ProfitMaxPaddingDoubleAuction

/**
 * TODO 売手が複数のリソースを提供する場合に収入決定部分がまだ対応できてない
 *
 * @property providers
 * @property requesters
 * @property default
 */
class VcgTrade(val providers: List<Bidder>, val requesters: List<Bidder>, val default: Config) {
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

    private fun solveWinRequestersProblem(winRequesters: List<Bidder>): Result {
        val conf = default.copy(lpFile = "Padding/winRequesters", provider = providers.size, requester = winRequesters.size)

        ProfitMaxDoubleAuction.makeLpFile(conf, Object.MAX, providers.plus(winRequesters))
        val cplex = LpImporter("LP/Padding/winRequesters").getCplex()

        cplex.solve()

        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val cplexValue = cplex.getValues(lp)
        val tempY = cplexValue.copyOfRange(0, winRequesters.map { it.bids.size }.sum())
        val y = Util.convertDimension(tempY, winRequesters.map { it.bids.size })
        val excludedXCplex = cplexValue.copyOfRange(winRequesters.map { it.bids.size }.sum(), cplexValue.lastIndex + 1)
        println("excludedXCplexSize ${excludedXCplex.size}")
        val x = Util.convertDimension4(excludedXCplex, winRequesters.map { it.bids.size }, providers.map { it.bids.size }, default)

        return Result(x, y, cplex.objValue)
    }

    data class Result(
            val x: List<List<List<DoubleArray>>>,
            val y: List<DoubleArray>,
            val objValue: Double
    )

    fun run(y: List<DoubleArray>, objValue: Double): ResultPre {
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
                providerRevenuesDensity
        )
    }

    private fun decideRequesterPayment(requesterId: Int, bidId: Int, excludedRequesters: List<Bidder>, objValue: Double): Double {
        // iを除いたオークションの目的関数の値を得る
        val conf = default.copy(lpFile = "Padding/auction\\{$requesterId}", requester = default.requester - 1)

        val bidders = providers.plus(excludedRequesters)
        ProfitMaxPaddingDoubleAuction.makeLpFile(conf, Object.MAX, bidders)
        val cplex = LpImporter("LP/Padding/auction\\{$requesterId}").getCplex()
        cplex.solve()

        // 支払い価格を導出する
        println("${requesters[requesterId].bids[bidId].getValue()}, ${cplex.objValue} $objValue")
        val pay = requesters[requesterId].bids[bidId].getValue() - (objValue - cplex.objValue)
        println("requester$requesterId,$bidId payment = $pay")

        return pay
    }

    private fun requesterPayments(y: List<DoubleArray>, objValue: Double) {
        y.forEachIndexed { j, bids ->
            bids.forEachIndexed { n, d ->
                if (isOne(d)) {
                    // 支払い価格を求める
                    val pay = decideRequesterPayment(j, n, requesters.filter { it.id != j }, objValue)
                    requesterCals[j].bids[n].addPayment(pay)
                    requesterCals[j].bids[n].addTime(requesters[j].bids[n].bundle.sum())
                    requesterCals[j].bids[n].addProfit(requesters[j].bids[n].value.tValue - pay)
                    providerBidResults.add(j, BidResult(arrayOf(j, n), pay, requesters[j].bids[n].value.tValue - pay))
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
                val quantity = resource.map { requester ->
                    if (requester.sum() > 0.1) trade += 1

                    requester.sum()
                }.sum()
                if (quantity > 0) {
                    if (!nealyEqual(providers[i].bids[r].bundle[r], quantity)) {
                        println("solution $quantity, available quantity ${providers[i].bids[r].bundle[r]}")
                    }

                    // 前半のvcgプライス部分
                    val conf = default.copy(lpFile = "Padding/reqAuction\\{$i}", provider = providers.size, requester = winRequesters.size)
                    winRequesters.forEach {
                        it.bids.forEach { bid ->
                            println("bid value = ${bid.value.getValue()}, ${bid.bundle.toList()}")
                        }
                    }

                    // 多分これで複数リソースへの対応はあっている(まだ上界値の方はあっていない気がする)
                    val newProviders = providers.mapIndexed { providerIndex, bidder ->
                        if (i == providerIndex) {
                            val bids = bidder.bids.mapIndexed { index, resource ->
                                if (r == index) {
                                    println("前半のvcg部分 provider $i resource $r")
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
                    ProfitMaxDoubleAuction.makeLpFile(conf, Object.MAX, newProviders.plus(winRequesters))

                    val cplex = LpImporter("LP/Padding/reqAuction\\{$i}").getCplex()
                    cplex.solve()

                    println("quantity * providers[i].bids[r].getValue() + objValue - cplex.objValue = $quantity * ${providers[i].bids[r].getValue()} + $objValue -${cplex.objValue}")
                    val vcg = quantity * providers[i].bids[r].getValue() + objValue - cplex.objValue

                    //  後半のpayoff部分
                    val supremum = getSupremum(i, r, quantity, paddingObjValue, requesters)
                    val payoff = getPayOff(i, r, supremum, winRequesters)

                    // 報酬額を決定する
                    val revenue = vcg - payoff

                    println("vcg - payoff = $vcg - $payoff")

                    println("provider_$i, resource_$r 's quantity = $quantity")
                    println("provider_$i, resource_$r 's revenue = $revenue")

                    providerCals[i].bids[r].addPayment(revenue)
                    providerCals[i].bids[r].addTime(quantity)
                    providerCals[i].bids[r].addProfit(revenue - quantity * providers[i].bids[r].value.tValue)
                    providerBidResults.add(BidResult(arrayOf(i, r), revenue, revenue - quantity * providers[i].bids[r].value.tValue))
                    providerRevenues.add(revenue / trade)
                    providerRevenuesDensity.add(revenue / quantity)
                }
            }
        }
    }

    private fun getPayOff(id: Int, resoruceId: Int, supremum: Double, winRequesters: List<Bidder>): Double {
        //  入札者を除くの代わりに提供リソースの量を0にする
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

        val conf1 = default.copy(provider = newProviders.size, requester = winRequesters.size, lpFile = "Padding/payoff-$id")

        ProfitMaxDoubleAuction.makeLpFile(conf1, Object.MAX, newProviders.plus(winRequesters))
        val cplex = LpImporter("LP/Padding/payoff-$id").getCplex()
        cplex.solve()

        val conf2 = default.copy(provider = newProviders.size - 1, requester = winRequesters.size, lpFile = "Padding/payoff\\{$id}")

        newProviders.forEachIndexed { index, bidder ->
            bidder.id = index
        }
        ProfitMaxDoubleAuction.makeLpFile(conf2, Object.MAX, newProviders.filter { it.id != id }.plus(winRequesters))
        val excludedCplex = LpImporter("LP/Padding/payoff\\{$id}").getCplex()
        excludedCplex.solve()

        return cplex.objValue - excludedCplex.objValue
    }

    private fun getSupremum(id: Int, resoruce: Int, quantity: Double, paddingObjValue: Double, requesters: List<Bidder>): Double {
        val conf = default.copy(provider = providers.size, requester = requesters.size, lpFile = "Padding/supremum\\{$id}")
        //  入札者を除くの代わりに提供リソースの量を0にする
        val newProviders = providers.mapIndexed { providerIndex, bidder ->
            if (id == providerIndex) {
                val bids = bidder.bids.mapIndexed { index, resource ->
                    if (resoruce == index) {
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
        ProfitMaxPaddingDoubleAuction.makeLpFile(conf, Object.MAX, newProviders.plus(requesters))

        val cplexVcg = LpImporter("LP/Padding/supremum\\{$id}").getCplex()

        cplexVcg.solve()

        return providers[id].bids[resoruce].getValue() * quantity + paddingObjValue - cplexVcg.objValue
    }
}