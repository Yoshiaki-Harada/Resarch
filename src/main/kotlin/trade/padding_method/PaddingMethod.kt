package trade.padding_method

import Util
import config.Config
import convert
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
        val excludedXCplex = cplexValue.copyOfRange(sum, sum + config.provider * config.resource * config.requester * config.bidNumber)
        val x = convert(excludedXCplex, config)
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
            println("q ${it.sum()}")
        }
        println("xsize = ${x.flatMap { it.flatMap { it.flatMap { it } } }.size}")
        println("excludedXCplex = ${excludedXCplex.size}")
        println("ysize = ${y.flatMap { it.toList() }.size}")
        println("qsize = ${q.flatMap { it.toList() }.size}")
        println("cplexValue = ${cplexValue.size}")

        providers.forEachIndexed { index, bidder ->
            bidder.id = index
        }


        val trade = VcgTrade(providers, requesters, config, x, q.map { it.toList() })
        val rs = trade.run(y, cplex.objValue)

        // 各企業の総リソース提供可能時間のリスト
        val p = providers.map { it.bids.map { it.bundle.sum() }.sum() }

        // 各企業の利益の合計を計算し，結果用のクラスに変換
        val providerResults = calProviderResult(p, rs, config)

        val requesterResults = rs.requesterCals.mapIndexed { j, it ->
            BidderResult(j, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }

        val sumProfit = rs.providerBidResults.map { it.profit }.sum().plus(rs.requesterBidResults.map { it.profit }.sum())

        if (rs.payments.isNullOrEmpty()) {
            println("**********取引は行われていません**********")
            rs.payments.add(0.0)
        }
        // sumPay
        // sumRevenue
        return Result(
                objectValue = objValue,
                sumCost = cost(x, providers, requesters),
                sumProfit = sumProfit,
                x = cplexValue,
                winBidNUmber = tempY.filter { isOne(it) }.size,
                providerResults = providerResults,
                requesterResults = requesterResults,
                providerProfitAve = providerResults.map { it.profit }.average(),
                providerProfitSD = providerResults.map { it.profit }.sd(),
                providerTimeRatioAve = providerResults.map { it.timeRatio }.average(),
                providerTimeRatioSD = providerResults.map { it.timeRatio }.sd(),
                requesterProfitAve = requesterResults.map { it.profit }.average(),
                requesterProfitSD = requesterResults.map { it.profit }.sd(),
                requesterPayAve = requesterResults.map { it.payment }.average(),
                requesterPaySD = requesterResults.map { it.payment }.sd(),
                providerRevenueAve = providerResults.map { it.payment }.average(),
                providerRevenueSD = providerResults.map { it.payment }.sd(),
                providerBidResults = rs.providerBidResults,
                requesterBidResults = rs.requesterBidResults,
                beforeProviderAvailabilityRatioAve = providerResults.map { it.beforeAvailabilityRatio }.average(),
                afterProviderAvailabilityRatioAve = providerResults.map { it.afterProviderAvailabilityRatio }.average(),
                providerRevenueDensityAve = rs.providerRevenueDensity.average(),
                providerRevenueDensitySD = rs.providerRevenueDensity.sd(),
                sumPay = rs.payments.sum(),
                sumRevenue = rs.providerRevenue.sum()
        )
    }
}