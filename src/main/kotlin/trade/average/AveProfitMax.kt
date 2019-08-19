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

object AveProfitMax : Trade {
    override
    fun run(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
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
        val y = xCplex.copyOfRange(0, sum)
        println("y_size: ${y.size}")
        val excludedXCplex = xCplex.copyOfRange(sum, xCplex.lastIndex + 1)
        println("x_size:" + excludedXCplex.size)
        val x = Util.convertDimension4(excludedXCplex, requesters.map { it.bids.size }, providers.map { it.bids.size }, config)

        // 解の表示
        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        println("x_$i$r$j$n = $d")
                    }
                }
            }
        }

        // 取引を行い利益等を計算する
        val rs = AveTrade(providers, requesters, config).run(x)

        // 各企業の総リソース提供可能時間のリスト
        val p = providers.map { it.bids.map { it.bundle.sum() }.sum() }

        // 各企業の利益の合計を計算し，結果用のクラスに変換
        val providerResults = TradeUtil.calProviderResult(p, rs, config)

        val requesterResults = rs.requesterCals.mapIndexed { j, it ->
            BidderResult(j, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }

        val sumProfit = rs.providerBidResults.map { it.profit }.sum().plus(rs.requesterBidResults.map { it.profit }.sum())

        println("payments ${rs.payments}")
        if (rs.payments.isNullOrEmpty()) {
            println("**********取引は行われていません**********")
            rs.payments.add(0.0)
        }
        return Result(
                objValue,
                TradeUtil.cost(x, providers, requesters),
                sumProfit,
                xCplex,
                y.filter { it == 1.0 }.size,
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
                providerResults.map { it.beforeAvailabilityRatio }.average(),
                providerResults.map { it.afterProviderAvailabilityRatio }.average(),
                rs.providerRevenueDensity.average(),
                rs.providerRevenueDensity.sd()
        )
    }
}
