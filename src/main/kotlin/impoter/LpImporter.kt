package impoter

import ilog.cplex.IloCplex

/*LPファイル名を受け取って，cplexを返す*/
class LpImporter(val filename: String) {
    fun getCplex(): IloCplex {
        val cplex = IloCplex()
        cplex.importModel("$filename.lp")
        return cplex
    }
}
