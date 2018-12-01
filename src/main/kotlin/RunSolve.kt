import Impoter.LpImporter

fun main(args: Array<String>) {
    val solver = Solver(LpImporter("LP/example2.lp").getCplex())
    solver.solve()
}

