package Impoter

import ilog.cplex.IloCplex
import java.io.File

/*LPファイル名を受け取って，cplexを返す*/
class LpImporter(val filename: String) {

    fun getCplex(): IloCplex {
        val cplex = IloCplex()
        cplex.importModel("$filename.lp")
        return cplex
    }

}
