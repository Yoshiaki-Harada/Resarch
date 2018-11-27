import ilog.concert.*;
import ilog.cplex.*;
import ilog.concert.IloLPMatrix
import ilog.concert.IloException


fun main(args: Array<String>) {
    println("Hello, world")

    val cplex = IloCplex()
    cplex.importModel("LP/example1.lp")

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

