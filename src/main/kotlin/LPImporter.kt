import ilog.cplex.IloCplex
import java.io.File


class LpImporter(val filename: String) {
    fun getCplex(): IloCplex {
        val cplex = IloCplex()
        cplex.importModel(filename)
        return cplex
    }
}
