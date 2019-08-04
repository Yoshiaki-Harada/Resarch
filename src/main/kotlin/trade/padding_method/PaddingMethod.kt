package trade.padding_method

import Util
import config.Config
import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex
import model.Bidder
import result.Result
import trade.Trade

object PaddingMethod : Trade {

    override fun run(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val cplexValue = cplex.getValues(lp)
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


        TODO()
    }
}