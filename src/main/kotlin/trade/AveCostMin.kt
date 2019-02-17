package trade

import config.Config
import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex
import model.Bidder
import result.*

//costMim用の方を作る
object AveCostMin : Trade {
    override
    fun trade(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
        //最適かの判定
        val status = cplex.status
        //目的関数値
        val objValue = cplex.objValue
        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val xCplex = cplex.getValues(lp)
        println("objValue = $objValue")

        val providers = bidders.subList(0, config.provider)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)
        val x = Util.convertDimension4(xCplex, requesters.map { it -> it.bids.size }, providers.map { it -> it.bids.size }, config)

        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        println("x_$i$r$j$n = $d")
                    }
                }
            }
        }

        //利益の計算用
        var providerCals = mutableListOf<BidderCal>()
        var requesterCals = mutableListOf<BidderCal>()
        initBidderCals(providerCals, providers)
        initBidderCals(requesterCals, requesters)

        val providerBidResults = mutableListOf<BidResult>()
        val requesterBidResults = mutableListOf<BidResult>()

        var payments = mutableListOf<Double>()

        //利益の計算
        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        //勝者となった入札に関して
                        if (d == 1.0) {
                            val payment = calPayment(providers[i], requesters[j], n, r)
                            payments.add(payment)
                            //提供側
                            providerCals[i].bids[r].addPayment(payment)
                            providerCals[i].bids[r].addProfit(calProviderProfit(payment, providers[i], requesters[j], n, r))
                            providerBidResults.add(BidResult(arrayOf(i, j, n, r), payment, calProviderProfit(payment, providers[i], requesters[j], n, r)))
                            //要求側
                            requesterCals[j].bids[n].addPayment(payment)
                            requesterCals[j].bids[n].addProfit(calRequesterProfit(payment, requesters[j], n, r))
                            requesterBidResults.add(BidResult(arrayOf(i, j, n, r), payment, calRequesterProfit(payment, requesters[j], n, r)))
                        }
                    }
                }
            }
        }

        //支払い価格と利益の合計の計算
        val providerResults = providerCals.mapIndexed { i, it ->
            BidderResult(i, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }

        val requesterResults = requesterCals.mapIndexed { j, it ->
            BidderResult(j, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }

        val sumProfit = providerBidResults.map { it.profit }.sum().plus(requesterBidResults.map { it.profit }.sum())

        return Result(
                objValue,
                objValue,
                sumProfit,
                xCplex,
                providerResults,
                requesterResults,
                providerBidResults.map { it.profit }.average(),
                Util.sd(providerBidResults.map { it.profit }),
                requesterBidResults.map { it.profit }.average(),
                Util.sd(requesterBidResults.map { it.profit }),
                payments.average(),
                Util.sd(payments),
                providerBidResults,
                requesterBidResults
        )
    }

    fun initBidderCals(bidderCals: MutableList<BidderCal>, bidders: List<Bidder>) {
        bidders.forEach {
            val bidCal = BidderCal()
            it.bids.forEach {
                bidCal.bids.add(BidCal())
            }
            bidderCals.add(bidCal)
        }
    }

    fun calRequesterBudgetDensity(requester: Bidder, bidIndex: Int, resource: Int): Double {
        return (requester.bids[bidIndex].getValue() * (requester.bids[bidIndex].bundle[resource] / requester.bids[bidIndex].bundle.sum())) / requester.bids[bidIndex].bundle[resource]
    }

    fun calPayment(provider: Bidder, requester: Bidder, bidIndex: Int, resource: Int): Double {
        //resourceに対する予算の密度
        val budgetOfResource = calRequesterBudgetDensity(requester, bidIndex, resource)
        //提供側と要求側の予算密度の平均
        val avePay = (provider.bids[resource].getValue() + budgetOfResource) / 2
        //                                       time
        return avePay * requester.bids[bidIndex].bundle[resource]
    }

    fun calProviderProfit(payment: Double, provider: Bidder, requester: Bidder, n: Int, r: Int): Double {
        //                                 cost                          time
        return payment - provider.bids[r].getValue() * requester.bids[n].bundle[r]
    }

    fun calRequesterProfit(payment: Double, requester: Bidder, bidIndex: Int, resource: Int): Double {
        //     resourceに対する予算の密度                                                             time
        return calRequesterBudgetDensity(requester, bidIndex, resource) * requester.bids[bidIndex].bundle[resource] - payment
    }
}