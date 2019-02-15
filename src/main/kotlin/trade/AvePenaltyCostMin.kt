package trade

import config.Config
import ilog.cplex.IloCplex
import model.Bidder
import result.Result

object AvePenaltyCostMin : Trade {
    override fun trade(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}