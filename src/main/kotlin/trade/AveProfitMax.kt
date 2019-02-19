package trade

import config.Config
import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex
import model.Bidder
import result.*
import sd

object AveProfitMax : Trade {
    override
    fun trade(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
        // 最適かの判定
        val status = cplex.status
        println("status = $status")
        // 目的関数値
        val objValue = cplex.objValue
        println("objValue = $objValue")

        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val xCplex = cplex.getValues(lp)
        val providers = bidders.subList(0, config.provider)
        println("providerNumber:" + providers.size)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)
        println("requesterNumber:" + requesters.size)
        val sum = requesters.map { it.bids.size }.sum()
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

        // 利益の計算
        val rs = AveTrade.run(x, providers, requesters)

        // 支払い価格と利益の合計の計算
        val providerResults = rs.providerCals.mapIndexed { i, it ->
            BidderResult(
                    i, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum()
            )
        }

        val requesterResults = rs.requesterCals.mapIndexed { j, it ->
            BidderResult(
                    j, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum()
            )
        }

        return Result(
                objValue,
                TradeUtil.cost(x, providers, requesters),
                objValue,
                xCplex,
                providerResults,
                requesterResults,
                providerResults.map { it.profit }.average(),
                providerResults.map { it.profit }.sd(),
                requesterResults.map { it.profit }.average(),
                requesterResults.map { it.profit }.sd(),
                rs.payments.average(),
                rs.payments.sd(),
                rs.providerBidResults,
                rs.requesterBidResults
        )
    }
}
