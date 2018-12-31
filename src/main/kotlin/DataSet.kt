import Impoter.LpImporter
import com.squareup.moshi.JsonWriter
import config.Config
import converter.BidderConverter
import lpfile.CostMinProviderAuction
import lpfile.lpformat.Object
import model.Bid
import model.Bidder
import model.Value

fun main(args: Array<String>) {
    val config = Config.fromJson("config")
    val p00 = Bid(Value(10.0, 0.0), listOf(1000.0, 0.0))
    val p01 = Bid(Value(20.0, 0.0), listOf(0.0, 200.0))
    val p0 = Bidder()
    p0.add(p00).add(p01)
    val p10 = Bid(Value(20.0, 0.0), listOf(100.0, 0.0))
    val p11 = Bid(Value(10.0, 0.0), listOf(0.0, 1000.0))
    val p1 = Bidder()
    p1.add(p10).add(p11)
    val r00 = Bid(Value(2000.0, 0.0), listOf(100.0, 0.0))
    val r01 = Bid(Value(3000.0, 0.0), listOf(50.0, 50.0))
    val r0 = Bidder()
    r0.add(r00).add(r01)
    val r10 = Bid(Value(4000.0, 0.0), listOf(25.0, 75.0))
    val r11 = Bid(Value(5000.0, 0.0), listOf(0.0, 100.0))
    val r1 = Bidder()
    r1.add(r10).add(r11)
    val bidders = listOf<Bidder>(p0, p1, r0, r1)

    CostMinProviderAuction.makeLpFile(config, Object.MIN, bidders)
    Solver(LpImporter(config.lpFile).getCplex()).solve()

}