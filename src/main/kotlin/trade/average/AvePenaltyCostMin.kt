package trade.average

import config.Config
import convert
import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex
import model.Bidder
import result.BidderResult
import result.Result
import rounding
import sd
import trade.Trade
import trade.TradeUtil
import trade.cost

object AvePenaltyCostMin : Trade {
    override fun run(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
        //最適かの判定
        val status = cplex.status
        println("status = $status")
        //目的関数値
        val objValue = cplex.objValue
        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val xCplex = cplex.getValues(lp).map { it.rounding() }
        println("objValue = $objValue")
        val providers = bidders.subList(0, config.provider)
        println("providerNumber:" + providers.size)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)
        println("requesterNumber:" + requesters.size)
        val sum = requesters.map { it.bids.size }.sum()
        println(sum)
        val y = xCplex.subList(0, sum)
        val excludedXCplex = xCplex.subList(sum, xCplex.lastIndex + 1)
        println("x_size:" + excludedXCplex.size)
        val x = convert(excludedXCplex, config)

        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        println("x_$i$r$j$n = $d")
                    }
                }
            }
        }

        // 取引を実行し利益等を計算する
        val rs = AveTrade(providers, requesters, config).run(x)
        // 各企業の総リソース提供時間のリスト
        val p = providers.map { it.bids.map { it.bundle.sum() }.sum() }

        // 各企業の利益の合計を計算し，結果用のクラスに変換
        val providerResults = TradeUtil.calProviderResult(p, rs, config)

        val requesterResults = rs.requesterCals.mapIndexed { j, it ->
            BidderResult(j, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }

        val sumProfit = rs.providerBidResults.map { it.profit }.sum().plus(rs.requesterBidResults.map { it.profit }.sum())

        return Result(
                objectValue = objValue,
                sumCost = cost(x, providers, requesters),
                sumProfit = sumProfit,
                x = xCplex,
                winBidNUmber = y.filter { it == 1.0 }.size,
                providerResults = providerResults,
                requesterResults = requesterResults,
                providerProfitAve = providerResults.map { it.profit }.average(),
                providerProfitSD = providerResults.map { it.profit }.sd(),
                providerTimeRatioAve = providerResults.map { it.timeRatio }.average(),
                providerTimeRatioSD = providerResults.map { it.timeRatio }.sd(),
                requesterProfitAve = requesterResults.map { it.profit }.average(),
                requesterProfitSD = requesterResults.map { it.profit }.sd(),
                requesterPayAve = rs.requesterBidResults.filter { it.payment == 0.0 }.map { it.payment }.average(),
                requesterPaySD = rs.requesterBidResults.filter { it.payment == 0.0 }.map { it.payment }.sd(),
                providerRevenueAve = rs.providerBidResults.filter { it.payment == 0.0 }.map { it.payment }.average(),
                providerRevenueSD = rs.providerBidResults.filter { it.payment == 0.0 }.map { it.payment }.sd(),
                providerBidResults = rs.providerBidResults,
                requesterBidResults = rs.requesterBidResults,
                beforeProviderAvailabilityRatioAve = providerResults.map { it.beforeAvailabilityRatio }.average(),
                afterProviderAvailabilityRatioAve = providerResults.map { it.afterProviderAvailabilityRatio }.average(),
                providerRevenueDensityAve = rs.providerRevenueDensity.average(),
                providerRevenueDensitySD = rs.providerRevenueDensity.sd(),
                sumPay = rs.requesterBidResults.filter { it.payment == 0.0 }.map { it.payment }.sum(),
                sumRevenue = rs.providerRevenue.sum(),
                liarResult = null
        )
    }
}