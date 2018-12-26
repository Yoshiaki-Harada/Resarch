import Impoter.JsonImporter
import Impoter.LpImporter
import config.Config
import model.Bidder

fun main(args: Array<String>) {
    // val solver = Solver(LpImporter("LP/example2").getCplex())
    //solver.solve()

    val bidders = ArrayList<Bidder>()
    val config = Config.fromJson("config")

    for (j in 0 until config.bidder) {
        val bidder = Bidder().fromJson(JsonImporter(config.bidderFile + j.toString()).getString())
        bidders.add(bidder)
    }

    VCG().start(config.lpFile, bidders, arrayOf(1.0, 1.0), config)
}
