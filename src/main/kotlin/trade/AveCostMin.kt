package trade

import config.Config
import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex
import model.Bidder
import result.*
import sd

object AveCostMin : Trade {
    override
    fun trade(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
        //最適かの判定
        val status = cplex.status
        println("status = $status")
        //目的関数値
        val objValue = cplex.objValue
        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val xCplex = cplex.getValues(lp)
        println("objValue = $objValue")
        val providers = bidders.subList(0, config.provider)
        println("providersNumber:" + providers.size)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)
        println("requesterNumber:" + requesters.size)
        val x = Util.convertDimension4(xCplex, requesters.map { it -> it.bids.size }, providers.map { it -> it.bids.size }, config)
        // 解を出力
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

        //支払い価格と利益の合計の計算
        val providerResults = rs.providerCals.mapIndexed { i, it ->
            BidderResult(i, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }

        val requesterResults = rs.requesterCals.mapIndexed { j, it ->
            BidderResult(j, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }

        //総利益の計算
        val sumProfit = rs.providerBidResults.map { it.profit }.sum().plus(rs.requesterBidResults.map { it.profit }.sum())

        return Result(
                objValue,
                TradeUtil.cost(x, providers, requesters),
                sumProfit,
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
