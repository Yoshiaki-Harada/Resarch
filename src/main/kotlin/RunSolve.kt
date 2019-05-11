import impoter.JsonImporter
import trade.VCG
import config.Config
import converter.BidderConverter
import model.Bidder
import model.Resource

fun main(args: Array<String>) {

    val bidders = ArrayList<Bidder>()
    val config = Config.fromJson("config")

    for (j in 0 until config.requester) {
        val bidder = BidderConverter.fromJson(JsonImporter(config.bidderFile + j.toString()).getString())
        bidders.add(bidder)
    }

    VCG.start(config.lpFile, bidders, Resource(arrayOf(1.0, 1.0)), config)

}
