package trade

import config.Config
import ilog.cplex.IloCplex
import model.Bidder
import result.Result

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
}


