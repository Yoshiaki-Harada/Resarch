package trade.padding_method

import Util
import config.Config
import convert
import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex
import model.Bidder
import result.Result
import rounding
import trade.Trade

object PaddingMethod : Trade {

    override fun run(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val solutions = cplex.getValues(lp).map { it.rounding() }
        val objValue = cplex.objValue
        val providers = bidders.subList(0, config.provider)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)

        val sum = requesters.map { it.bids.size }.sum()
        val y = Util.convertDimension(solutions.subList(0, sum), requesters.map { it.bids.size })

        val excludedYSolutions = solutions.subList(sum, sum + config.provider * config.resource * config.requester * config.bidNumber)
        val x = convert(excludedYSolutions, config)

        val tempQ = solutions.subList(solutions.lastIndex + 1 - config.provider * config.resource, solutions.lastIndex + 1)
        val q = Util.convertDimension(tempQ, List(providers.size) { config.resource })

        providers.forEachIndexed { index, bidder ->
            bidder.id = index
        }

        requesters.forEachIndexed { index, bidder ->
            bidder.id = index
        }

        val trade = VcgTrade(providers, requesters, config, x, q.map { it.toList() })
        val rs = trade.run(y, cplex.objValue)

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
                auctioneerProfit = rs.payments.sum() - rs.providerRevenue.sum(),
                lieRequesterNUmber = config.lieRequesterNumber
        )
    }
}