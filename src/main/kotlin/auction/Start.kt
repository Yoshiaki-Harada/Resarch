package auction

import Impoter.JsonImporter
import Impoter.LpImporter
import config.Config
import converter.BidderConverter
import converter.ResultConverter
import cplex.Solver
import cplex.lpformat.Object
import model.Bidder
import trade.AveCostMin
import trade.AvePenaltyCostMin
import trade.AveProfitMax
import trade.Trade
import winner.*


fun main(args: Array<String>) {
    val config = Config.fromJson("config")

    //入札作成
    val bidders = mutableListOf<Bidder>()
    for (index in 0 until config.provider + config.requester) {
        bidders.add(BidderConverter.fromJson(JsonImporter(config.bidderFile + "$index").getString()))
    }

    val lpMaker: LpMaker = when (config.auction) {
        0 -> SingleSidedAuction
        1 -> CostMinProviderAuction
        2 -> ProfitMaxDoubleAuction
        3 -> CostMinPenaltyAuction
        else -> {
            println("error")
            //とりあえず
            SingleSidedAuction
        }
    }

    val obj: Object = when (config.auction) {
        0 -> Object.MAX
        1 -> Object.MIN
        2 -> Object.MAX
        3 -> Object.MIN
        else -> {
            println("error")
            //とりあえず
            Object.MIN
        }
    }

    val trade: Trade = when (config.auction) {
        //Todo:0->VCG
        1 -> AveCostMin
        2 -> AveProfitMax
        3 -> AvePenaltyCostMin
        else -> {
            println("error")
            //とりあえず
            AveCostMin
        }
    }

    lpMaker.makeLpFile(config, obj, bidders)

    //勝者決定と取引価格決定
    val cplex = Solver(LpImporter(config.lpFile).getCplex()).solve()
    val result = trade.trade(cplex, bidders, config)
    writer.JsonWriter(config.resultFile).makeFile(ResultConverter.toJson(result))

}