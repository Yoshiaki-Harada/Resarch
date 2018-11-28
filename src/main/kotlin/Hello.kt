import ilog.concert.*;
import ilog.cplex.*;
import ilog.concert.IloLPMatrix
import ilog.concert.IloException


fun main(args: Array<String>) {
    val solver = Solver(LpImporter("LP/example1.lp").getCplex())
    solver.solve()
}

