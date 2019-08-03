import config.Config
import cplex.lpformat.Object
import model.Bid
import model.Bidder
import model.Value
import winner.ProfitMaxPaddingDoubleAuction

fun main(args: Array<String>) {

    val config = Config.fromJson("config-test")

    val bids0 = listOf<Bid>(
            Bid(Value(10.0, 0.0), listOf(5.0, 0.0, 0.0)),
            Bid(Value(10.0, 0.0), listOf(0.0, 0.0, 0.0)),
            Bid(Value(10.0, 0.0), listOf(0.0, 0.0, 0.0)))

    val bids1 = listOf<Bid>(
            Bid(Value(10.0, 0.0), listOf(0.0, 0.0, 0.0)),
            Bid(Value(10.0, 0.0), listOf(0.0, 7.0, 0.0)),
            Bid(Value(10.0, 0.0), listOf(0.0, 0.0, 0.0)))

    val bids2 = listOf<Bid>(
            Bid(Value(10.0, 0.0), listOf(0.0, 0.0, 0.0)),
            Bid(Value(10.0, 0.0), listOf(0.0, 0.0, 0.0)),
            Bid(Value(10.0, 0.0), listOf(0.0, 0.0, 10.0)))

    val provider0 = Bidder().add(bids0)
    val provider1 = Bidder().add(bids1)
    val provider2 = Bidder().add(bids2)

    val requester0 = Bidder(Bid(Value(20.0, 0.0), listOf(2.0, 1.0, 0.0)))
    val requester1 = Bidder(Bid(Value(20.0, 0.0), listOf(6.0, 1.0, 4.0)))
    val requester2 = Bidder(Bid(Value(20.0, 0.0), listOf(2.0, 0.0, 5.0)))

    val bidders = listOf(provider0, provider1, provider2, requester0, requester1, requester2)

    ProfitMaxPaddingDoubleAuction.makeLpFile(config, Object.MAX, bidders)
}
