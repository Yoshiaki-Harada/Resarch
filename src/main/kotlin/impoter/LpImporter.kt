package impoter

import ilog.cplex.IloCplex

/**
 * file名を受け取ってIloCplexオブジェクトを返す
 *
 * @property filename
 */
class LpImporter(val filename: String) {
    fun getCplex(): IloCplex {
        val cplex = IloCplex()
        cplex.importModel("$filename.lp")
        return cplex
    }
}
