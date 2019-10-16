package trade

import config.Config
import ilog.cplex.IloCplex
import model.Bidder
import result.BidderResult
import result.LiarResult
import result.Result
import sd

interface Trade {

    /**
     * 取引を実行する
     *
     * @param cplex
     * @param bidders
     * @param config
     * @return
     */
    fun run(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result

    fun getResult(
            config: Config,
            objValue: Double,
            providers: List<Bidder>,
            requesters: List<Bidder>,
            resultPre: ResultPre,
            x: List<List<List<List<Double>>>>,
            y: List<List<Double>>,
            solutions: List<Double>,
            lieProviderNumber: Int): Result {

        // 各企業の総リソース提供可能時間のリスト
        val p = providers.map { it.bids.map { it.bundle.sum() }.sum() }

        // 各企業の利益の合計を計算し，結果用のクラスに変換
        val providerResults = calProviderResult(p, resultPre, config)

        val requesterResults = resultPre.requesterCals.mapIndexed { j, it ->
            BidderResult(j, it.bids.map { it.payment }.sum(), it.bids.map { it.profit }.sum())
        }

        val sumProfit = resultPre.providerBidResults.map { it.profit }.sum().plus(resultPre.requesterBidResults.map { it.profit }.sum())

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
                sumPay = resultPre.payments.sum(),
                sumRevenue = resultPre.providerRevenue.sum(),
                liarResult = LiarResult(
                        providerProfitAve = providerResults.filter { it.id < lieProviderNumber }.map { it.profit }.average(),
                        providerProfitSD = providerResults.filter { it.id < lieProviderNumber }.map { it.profit }.sd(),
                        providerRevenueDensityAve = resultPre.providerRevenueDensity.filterIndexed { index, providerResult -> index < lieProviderNumber }.average(),
                        providerRevenueDensitySD = resultPre.providerRevenueDensity.filterIndexed { index, providerResult -> index < lieProviderNumber }.sd()
                )
        )
    }
}


