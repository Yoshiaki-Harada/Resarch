package trade

import config.Config
import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex
import model.Bidder
import result.BidderResult
import result.ProviderLiarResult
import result.RequesterLiarResult
import result.Result
import rounding
import sd
import trade.padding_method.PaddingMethod

interface Trade {

    /**
     * 取引を実行する
     *
     * @param cplex
     * @param bidders
     * @param config
     * @return
     */
    fun run(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val solutions = cplex.getValues(lp).map { it.rounding() }
        cplex.objValue
        return PaddingMethod.run(solutions, cplex.objValue, bidders, config)

    }

    /**
     * すでにCplexで解いた結果をもう一度集計しなおす
     *
     * @param solutions
     * @param objValue
     * @param bidders
     * @param config
     * @return
     */
    fun run(solutions: List<Double>, objValue: Double, bidders: List<Bidder>, config: Config): Result

    fun getResult(
            config: Config,
            objValue: Double,
            providers: List<Bidder>,
            requesters: List<Bidder>,
            resultPre: ResultPre,
            x: List<List<List<List<Double>>>>,
            y: List<List<Double>>,
            solutions: List<Double>,
            lieProviderNumber: Int,
            auctioneerProfit: Double,
            lieRequesterNUmber: Int): Result {

        // 各企業の総リソース提供可能時間のリスト
        val p = providers.map { it.bids.map { it.bundle.sum() }.sum() }

        // 各企業の利益の合計を計算し，結果用のクラスに変換
        val providerResults = calProviderResult(p, resultPre, config)

        val requesterResults = resultPre.requesterCals.mapIndexed { j, it ->
            BidderResult(j, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }
        print("reques")
//        val sumProfit = resultPre.providerBidResults.map { it.profit }.sum().plus(resultPre.requesterBidResults.map { it.profit }.sum())
        val sumProfit = providerResults.map { it.profit }.sum() + requesterResults.map { it.profit }.sum()
        if (resultPre.payments.isNullOrEmpty()) {
            println("**********取引は行われていません**********")
            resultPre.payments.add(0.0)
        }
        return Result(
                objectValue = objValue,
                sumCost = cost(x, providers, requesters),/*このxではなくP(I,J)のx*/
                sumProfit = sumProfit,
                x = solutions,
                winBidNUmber = y.flatMap { it }.filter { isOne(it) }.size,
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
                providerBidResults = resultPre.providerBidResults,
                requesterBidResults = resultPre.requesterBidResults,
                beforeProviderAvailabilityRatioAve = providerResults.map { it.beforeAvailabilityRatio }.average(),
                afterProviderAvailabilityRatioAve = providerResults.map { it.afterProviderAvailabilityRatio }.average(),
                providerRevenueDensityAve = resultPre.providerRevenueDensity.average(),
                providerRevenueDensitySD = resultPre.providerRevenueDensity.sd(),
                sumPay = providerResults.map { it.payment }.sum(),
                sumRevenue = requesterResults.map { it.payment }.sum(),
                providerLiarResult = ProviderLiarResult(
                        providerProfitAve = providerResults.filter { it.id < lieProviderNumber }.map { it.profit }.average().nanTo0(),
                        providerProfitSD = providerResults.filter { it.id < lieProviderNumber }.map { it.profit }.sd().nanTo0(),
                        providerRevenueDensityAve = resultPre.providerRevenueDensity.filterIndexed { index, providerResult -> index < lieProviderNumber }.average().nanTo0(),
                        providerRevenueDensitySD = resultPre.providerRevenueDensity.filterIndexed { index, providerResult -> index < lieProviderNumber }.sd().nanTo0()
                ),
                auctioneerProfit = auctioneerProfit,
                requesterLiarResult = RequesterLiarResult(
                        requesterProfitAve = requesterResults.filter { it.id < lieRequesterNUmber }.map { it.profit }.average().nanTo0(),
                        requesterProfitSD = requesterResults.filter { it.id < lieRequesterNUmber }.map { it.profit }.sd().nanTo0(),
                        requesterPayAve = requesterResults.filter { it.id < lieRequesterNUmber }.map { it.payment }.average().nanTo0(),
                        requesterPaySD = requesterResults.filter { it.id < lieRequesterNUmber }.map { it.payment }.sd().nanTo0()
                )
        )
    }
}

inline fun Double.nanTo0(): Double {
    return if (this.isNaN()) 0.0
    else this
}

