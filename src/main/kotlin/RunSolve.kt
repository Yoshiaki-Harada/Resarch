import config.Config
import cplex.lpformat.Object
import impoter.LpImporter
import model.Bid
import model.Bidder
import model.Value
import trade.padding_method.PaddingMethod
import winner.ProfitMaxPaddingDoubleAuction

fun main(args: Array<String>) {

    val config = Config.fromJson("default-test")

    val bids0 = listOf<Bid>(
            Bid(Value(1.0, 0.0), listOf(2.0, 0.0)),
            Bid(Value(0.0, 0.0), listOf(0.0, 0.0)))

    val bids1 = listOf<Bid>(
            Bid(Value(2.0, 0.0), listOf(1.0, 0.0)),
            Bid(Value(0.0, 0.0), listOf(0.0, 0.0)))

    val bids2 = listOf<Bid>(
            Bid(Value(4.0, 0.0), listOf(1.0, 0.0)),
            Bid(Value(0.0, 0.0), listOf(0.0, 0.0)))

    val bids3 = listOf<Bid>(
            Bid(Value(0.0, 0.0), listOf(0.0, 0.0)),
            Bid(Value(0.0, 1.0), listOf(0.0, 1.0)))

    val bids4 = listOf<Bid>(
            Bid(Value(0.0, 0.0), listOf(0.0, 0.0)),
            Bid(Value(0.0, 1.0), listOf(0.0, 1.0)))

    val provider0 = Bidder().add(bids0)
    val provider1 = Bidder().add(bids1)
    val provider2 = Bidder().add(bids2)
    val provider3 = Bidder().add(bids3)
    val provider4 = Bidder().add(bids4)

    val p1 = listOf<Bid>(
            Bid(Value(9.0, 0.0), listOf(2.0, 0.0)),
            Bid(Value(3.0, 0.0), listOf(0.0, 1.0)))
    val requester0 = Bidder().add(p1)

    val bidders = listOf(provider0, provider1, provider2, provider3, provider4, requester0)

    ProfitMaxPaddingDoubleAuction.makeLpFile(config, Object.MAX, bidders)

    val cplex = LpImporter("LP/test").getCplex()

    cplex.solve()

    PaddingMethod.run(cplex, bidders, config)
}

fun getPayoff(id: Int, resoruce: Int, quantity: Double, providers: List<Bidder>, winRequesters: List<Bidder>, config: Config): Double {
    config.provider = providers.size
    config.requester = winRequesters.size
    config.lpFile = "Padding/vcg"
    ProfitMaxPaddingDoubleAuction.makeLpFile(config, Object.MAX, providers.plus(winRequesters))

    val cplex = LpImporter("Padding/vcg").getCplex()
    cplex.solve()

    config.provider = providers.size - 1
    config.requester = winRequesters.size
    config.lpFile = "Padding/payoff\\{$id}"
    ProfitMaxPaddingDoubleAuction.makeLpFile(config, Object.MAX, providers.filter { it.id != id }.plus(winRequesters))
    val cplexVcg = LpImporter("Padding/vcg").getCplex()

    return providers[id].bids[resoruce].getValue() * quantity + cplex.objValue - cplexVcg.objValue
}
