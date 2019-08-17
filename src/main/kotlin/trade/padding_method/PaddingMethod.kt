package trade.padding_method

import Util
import config.Config
import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex
import model.Bidder
import result.BidderResult
import result.Result
import sd
import trade.Trade
import trade.calProviderResult
import trade.cost
import trade.isOne

object PaddingMethod : Trade {

    override fun run(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val cplexValue = cplex.getValues(lp)
        val objValue = cplex.objValue
        val providers = bidders.subList(0, config.provider)
        println("providerNumber:" + providers.size)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)
        println("requesterNumber:" + requesters.size)
        val sum = requesters.map { it.bids.size }.sum()
        val tempY = cplexValue.copyOfRange(0, sum)
        val y = Util.convertDimension(tempY, requesters.map { it.bids.size })
        val excludedXCplex = cplexValue.copyOfRange(sum, cplexValue.lastIndex - config.resource + 1)
        val x = Util.convertDimension4(excludedXCplex, requesters.map { it.bids.size }, providers.map { it.bids.size }, config)
        val tempQ = cplexValue.copyOfRange(cplexValue.lastIndex + 1 - config.provider * config.resource, cplexValue.lastIndex + 1)
        val q = Util.convertDimension(tempQ, List(providers.size) { config.resource })

        y.forEachIndexed { index, doubles ->
            println("y$index=${doubles.toList()}")
        }

        x.forEachIndexed { i, provider ->
            provider.forEachIndexed { r, resource ->
                resource.forEachIndexed { j, requester ->
                    requester.forEachIndexed { n, d ->
                        print("x_$i$r$j$n = $d, ")
                    }
                }
            }
            println()
        }

        q.forEach {
            println("q ${it.toList()}")
        }

        providers.forEachIndexed { index, bidder ->
            bidder.id = index
        }

        val trade = VcgTrade(providers, requesters, config)
        val rs = trade.run(y, cplex.objValue)

        // 各企業の総リソース提供可能時間のリスト
        val p = providers.map { it.bids.map { it.bundle.sum() }.sum() }

        // 各企業の利益の合計を計算し，結果用のクラスに変換
        val providerResults = calProviderResult(p, rs, config)

        val requesterResults = rs.requesterCals.mapIndexed { j, it ->
            BidderResult(j, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }

        val sumProfit = rs.providerBidResults.map { it.profit }.sum().plus(rs.requesterBidResults.map { it.profit }.sum())

        return Result(
                objValue,
                cost(x, providers, requesters),
                sumProfit,
                cplexValue,
                tempY.filter { isOne(it) }.size,
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
                rs.providerRewards.average(),
                rs.providerRewards.sd(),
                rs.providerBidResults,
                rs.requesterBidResults,
                providerResults.map { it.beforeAvailabilityRatio }.average(),
                providerResults.map { it.afterProviderAvailabilityRatio }.average(),
                rs.providerRewardDensity.average(),
                rs.providerRewardDensity.sd()
        )
    }
}