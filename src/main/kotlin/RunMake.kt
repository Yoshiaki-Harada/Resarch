import Impoter.JsonImporter
import config.Config
import converter.BidderConverter
import winner.SingleSidedAuction
import cplex.lpformat.Object
import model.Bidder
import model.Resource

fun main(args: Array<String>) {

    val config = Config.fromJson("config")
    val bidders: MutableList<Bidder> = mutableListOf()
    for (j in 0 until config.requester) {
        val bidder = BidderConverter.fromJson(JsonImporter(config.bidderFile + j.toString()).getString())
        bidders.add(bidder)
    }
    SingleSidedAuction.makeLpFile(config, Object.MAX, bidders, Resource(arrayOf(1.0, 1.0)))

}
