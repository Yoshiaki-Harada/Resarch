import Impoter.JsonImporter
import Impoter.LpImporter

fun main(args: Array<String>) {
   // val solver = Solver(LpImporter("LP/example2").getCplex())
    //solver.solve()

    val bidders = ArrayList<Bidder>()
    val i = 3
    for (j in  0 until 3){
        val bidder = Bidder().fromJson(JsonImporter("Bid/Bidder" + j.toString()).getString())
        bidders.add(bidder)
    }

    VCG().start("LP/example2",bidders)
}
