package trade

import config.Config
import ilog.cplex.IloCplex
import model.Bidder
import result.Result

interface Trade {
    fun run(cplex: IloCplex, bidders: List<Bidder>, config: Config): Result
}


