import Impoter.JsonImporter
import Impoter.LpImporter
import config.Config
import converter.BidderConverter
import cplex.lpformat.Object
import ilog.cplex.IloCplex
import model.Bid
import model.Bidder
import model.Value
import winner.CostMinPenaltyAuction
import writer.JsonWriter
import java.util.*
import kotlin.streams.toList

fun main(args: Array<String>) {

    val config = Config.fromJson("config")
    val bidders = mutableListOf<Bidder>()
    for (index in 0 until config.provider + config.requester) {
        bidders.add(BidderConverter.fromJson(JsonImporter(config.bidderFile + "$index").getString()))
    }

    val copyBidders = bidders
            .subList(0, config.provider)
            .map {
                Bidder().add(
                        it.bids.map {
                            Bid(Value(it.value.tValue, 1.5 * it.value.tValue), it.bundle)
                        }
                )
            }

    val afterBidders = copyBidders.plus(bidders.subList(config.provider, config.provider + config.requester))

    afterBidders.forEachIndexed { index, bidder ->
        JsonWriter(config.bidderFile + "Single$index").makeFile(BidderConverter.toJson(bidder))
    }

}