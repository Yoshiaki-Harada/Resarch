import Impoter.JsonImporter
import Impoter.LpImporter
import config.Config
import converter.BidderConverter
import cplex.lpformat.Object
import ilog.cplex.IloCplex
import model.Bidder
import winner.CostMinPenaltyAuction
import java.util.*
import kotlin.streams.toList

fun main(args: Array<String>) {
    val config = Config.fromJson("config")
    val bidders = mutableListOf<Bidder>()
    for (index in 0 until config.provider + config.requester) {
        bidders.add(BidderConverter.fromJson(JsonImporter(config.bidderFile + "$index").getString()))
    }

    CostMinPenaltyAuction.makeLpFile(config, Object.MIN,bidders)

    val cplex = LpImporter("lp").getCplex()
    cplex.ray
}