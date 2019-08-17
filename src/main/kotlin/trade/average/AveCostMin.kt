package trade.average

import Util
import config.Config
import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex
import model.Bidder
import result.BidderResult
import result.Result
import sd
import trade.Trade
import trade.TradeUtil

/**
 *　要求が制約になっているので，満たせない場合解けない
 */
object AveCostMin : Trade {
    override
    fun run(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
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

        val x = Util.convertDimension4(xCplex, requesters.map { it.bids.size }, providers.map { it.bids.size }, config)
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
        val rs = AveTrade(providers, requesters, config).run(x)

        // 各企業のリソース提供時間のリスト
        val p = providers.map { it.bids.map { it.bundle.sum() }.sum() }

        // 各企業の利益の合計を計算し，結果用のクラスに変換
        val providerResults = TradeUtil.calProviderResult(p, rs, config)


        val requesterResults = rs.requesterCals.mapIndexed { j, it ->
            BidderResult(j, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }

        val sumProfit = rs.providerBidResults.map { it.profit }.sum().plus(rs.requesterBidResults.map { it.profit }.sum())

        return Result(
                objValue,
                TradeUtil.cost(x, providers, requesters),
                sumProfit,
                xCplex,
                -1,
                providerResults,
                requesterResults,
                providerResults.map { it.profit }.average(),
                providerResults.map { it.profit }.sd(),
                providerResults.map { it.timeRatio }.average(),
                providerResults.map { it.timeRatio }.sd(),
                requesterResults.map { it.profit }.average(),
                requesterResults.map { it.profit }.sd(),
                rs.payments.average(),
                rs.payments.sd(),
                rs.payments.average(),
                rs.payments.sd(),
                rs.providerBidResults,
                rs.requesterBidResults,
                TODO(),
                TODO(),
                TODO(),
                TODO()
        )
    }
}
