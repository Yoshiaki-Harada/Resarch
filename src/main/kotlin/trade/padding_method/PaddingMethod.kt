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

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}