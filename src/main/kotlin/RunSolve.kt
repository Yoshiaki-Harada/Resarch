import lpformat.Bounds
import lpformat.Object

fun main(args: Array<String>) {
    val solver = Solver(LpImporter("LP/example2.lp").getCplex())
    solver.solve()
}

