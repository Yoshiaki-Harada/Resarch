package trade

import config.Config
import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex
import model.Bidder
import result.*

object AveProfitMax : Trade {
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
        println("provider:" + providers.size)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)
        val sum = requesters.map { it -> it.bids.size }.sum()
        println(sum)
        val excludedXCplex = xCplex.copyOfRange(sum, xCplex.lastIndex + 1)
        println("x_size:" + excludedXCplex.size)
        val x = Util.convertDimension4(excludedXCplex, requesters.map { it -> it.bids.size }, providers.map { it -> it.bids.size }, config)

        var cost = 0.0
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jに提供するとき1となる変数
                        //provider_iがresource_rをrequester_jの入札の要求resource_mに提供する時間x(正の整数)
                        cost += resource.getValue() * bid.bundle[r] * x[i][r][j][n]
                    }
                }
            }
        }

        println("cost = $cost")

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

        println("provider:" + providers.size)

        println("providerCals:" + providerCals.size)

        //利益の計算
        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        if (d == 1.0) {
                            val payment = calPayment(providers[i], requesters[j], n, r)
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
        val providerResults = mutableListOf<BidderResult>()
        val requesterResults = mutableListOf<BidderResult>()

        providerCals.forEachIndexed { i, it ->
            providerResults.add(BidderResult(i, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum()))
        }

        requesterCals.forEachIndexed { j, it ->
            requesterResults.add(BidderResult(j, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum()))
        }

        return Result(objValue, cost, objValue, xCplex, providerResults, requesterResults, providerBidResults, requesterBidResults)

    }

    fun initBidderCals(bidderCals: MutableList<BidderCal>, bidders: List<Bidder>) {
        println("bidders:" + bidders.size)
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
