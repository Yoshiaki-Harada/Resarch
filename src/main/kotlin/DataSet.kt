import com.squareup.moshi.JsonWriter
import config.Config
import converter.BidderConverter
import model.Bid
import model.Bidder
import model.Value

fun main(args: Array<String>) {
    val config = Config.fromJson("config")

    val bid0 = Bid(Value(0.0, 0.0), listOf(0.0, 0.0))
    val bid1 = Bid(Value(0.0, 0.0), listOf(0.0, 0.0))
    val bidder0 = Bidder()
    bidder0.add(bid0)
    bidder0.add(bid1)

    writer.JsonWriter("bidder").makeFile(BidderConverter.toJson(bidder0))
}