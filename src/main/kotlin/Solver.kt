import ilog.concert.IloLPMatrix
import ilog.cplex.IloCplex

class Solver(val cplex: IloCplex){
    fun solve() {
        if (cplex.solve()) {
            println("Solution status = " + cplex.getStatus())
            println("Solution status = " + cplex.getObjValue())
            val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
            val x = cplex.getValues(lp)
            for (j in x.indices) {
                println("Variable " + j + ": Value = " + x[j])
            }
        }
        cplex.end()
    }
}


