package trade.average

import Util
import config.Config
import convert
import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex
import model.Bidder
import result.Result
import rounding
import trade.Trade

object AvePenaltyCostMin : Trade {
    override fun run(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
        val objValue = cplex.objValue
        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val solutions = cplex.getValues(lp).map { it.rounding() }
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

        return this.getResult(
                config = config,
                objValue = objValue,
                providers = providers,
                requesters = requesters,
                x = x,
                y = y,
                solutions = solutions,
                resultPre = rs,
                lieProviderNumber = 0
        )
    }
}