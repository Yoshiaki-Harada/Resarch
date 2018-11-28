import ilog.cplex.IloCplex
import java.io.File

open class Importer

class LpImporter (val filename :String) : Importer() {

    fun getCplex() : IloCplex {
        val cplex = IloCplex()
        cplex.importModel(filename)
        return cplex
    }

}
