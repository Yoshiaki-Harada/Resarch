package trade

import config.Config
import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex
import model.Bidder
import result.*

object AveProfitMax : Trade {
    override
    fun trade(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
        // 最適かの判定
        val status = cplex.status
        println("status = $status")
        // 目的関数値
        val objValue = cplex.objValue
        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val xCplex = cplex.getValues(lp)
        println("objValue = $objValue")
        val providers = bidders.subList(0, config.provider)
        println("providerNumber:" + providers.size)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)
        println("requesterNumber:" + requesters.size)
        val sum = requesters.map { it.bids.size }.sum()
        println(sum)
        val excludedXCplex = xCplex.copyOfRange(sum, xCplex.lastIndex + 1)
        println("x_size:" + excludedXCplex.size)
        val x = Util.convertDimension4(excludedXCplex, requesters.map { it.bids.size }, providers.map { it.bids.size }, config)

        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        println("x_$i$r$j$n = $d")
                    }
                }
            }
        }

        // 利益の計算用クラスの準備
        var providerCals = mutableListOf<BidderCal>()
        var requesterCals = mutableListOf<BidderCal>()
        TradeUtil.initBidderCals(providerCals, providers)
        TradeUtil.initBidderCals(requesterCals, requesters)

        val providerBidResults = mutableListOf<BidResult>()
        val requesterBidResults = mutableListOf<BidResult>()

        val payments = mutableListOf<Double>()

        // 利益の計算
        AveTrade.run(x, providers, requesters, payments, providerCals, providerBidResults, requesterCals, requesterBidResults)

        // 支払い価格と利益の合計の計算
        val providerResults = providerCals.mapIndexed { i, it ->
            BidderResult(i, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }

        val requesterResults = requesterCals.mapIndexed { j, it ->
            BidderResult(j, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }

        return Result(
                objValue,
                TradeUtil.cost(x, providers, requesters),
                objValue,
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
}
