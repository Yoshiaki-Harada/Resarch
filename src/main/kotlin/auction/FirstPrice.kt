package auction

import Impoter.LpImporter
import config.Config
import ilog.concert.IloLPMatrix
import model.Bidder
import model.Resource

object FirstPrice : SingleSidedAuction {
    fun start(lpfile: String, bidders: List<Bidder>, config: Config) {
        val cplex = LpImporter(lpfile).getCplex()
        cplex.solve()
        //最適かの判定
        val status = cplex.status
        //目的関数値
        val objValue = cplex.objValue
        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val xCplex = cplex.getValues(lp)
        val providers = bidders.subList(0, config.provider)
        val requesters = bidders.subList(config.provider, config.requester)

        val map = providers.map { it -> it.bids.size }
        val x = Util.convertDimension(xCplex, listOf())

    }
}