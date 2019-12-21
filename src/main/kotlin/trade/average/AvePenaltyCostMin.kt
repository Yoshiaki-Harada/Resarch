package trade.average

import Util
import config.Config
import convert
import model.Bidder
import result.Result
import trade.Trade

object AvePenaltyCostMin : Trade {
    override fun run(solutions: List<Double>, objValue: Double, bidders: List<Bidder>, config: Config, startTimeMillis: Long): Result {
        val providers = bidders.subList(0, config.provider)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)

        val sum = requesters.map { it.bids.size }.sum()
        val y = Util.convertDimension(solutions.subList(0, sum), requesters.map { it.bids.size })
        val x = convert(solutions.subList(sum, solutions.lastIndex + 1), config)

        providers.forEachIndexed { index, bidder ->
            bidder.id = index
        }

        requesters.forEachIndexed { index, bidder ->
            bidder.id = index
        }

        // 取引を実行し利益等を計算する
        val rs = AveTrade(providers, requesters, config).run(x)
        val end = System.currentTimeMillis();

        return this.getResult(
                config = config,
                objValue = objValue,
                providers = providers,
                requesters = requesters,
                resultPre = rs,
                x = x,
                y = y,
                solutions = solutions,
                lieProviderNumber = config.lieProviderNumber,
                auctioneerProfit = 0.0,
                lieRequesterNUmber = config.lieRequesterNumber,
                startTimeMillis = startTimeMillis,
                endTimeMillis = end
        )
    }
}