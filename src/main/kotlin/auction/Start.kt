package auction

import impoter.JsonImporter
import impoter.LpImporter
import config.Config
import converter.BidderConverter
import converter.ResultConverter
import cplex.Solver
import cplex.lpformat.Object
import model.Bidder
import trade.*
import trade.average.AveCostMin
import trade.average.AvePenaltyCostMin
import trade.average.AveProfitMax
import trade.provider_single.SingleCostMin
import winner.*
import writer.JsonWriter
import writer.Saver

/*
0 VCG用
1 コスト最小化
2 利益最大化 取引（平均）
3 コスト最小化（ペナルティ）取引（平均）
4~　コスト最小化（ペナルティ）取引（提供単価）
 */
fun main(args: Array<String>) {
    val config = Config.fromJson("config")
    run(config)
}

fun run(config: Config) {
    //入札作成
    val bidders = mutableListOf<Bidder>()

    for (index in 0 until config.provider + config.requester) {
        bidders.add(BidderConverter.fromJson(JsonImporter("${config.bidderFile}/bidder$index").getString()))
    }

    val lpMaker: LpMaker = when (config.auction) {
        0 -> SingleSidedAuction
        1 -> CostMinProviderAuction
        2 -> ProfitMaxDoubleAuction
        3 -> CostMinPenaltyAuction
        4, 5, 6, 7, 8, 9, 10, 11 -> CostMinPenaltyAuction
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
        4, 5, 6, 7, 8, 10, 11 -> Object.MIN
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
        4, 5, 6, 7, 8, 9, 10, 11 -> SingleCostMin
        else -> {
            println("error")
            //とりあえずO
            AveCostMin
        }
    }

    lpMaker.makeLpFile(config, obj, bidders)

    //勝者決定と取引価格決定
    val cplex = Solver(LpImporter(config.lpFile).getCplex()).solve()
    val result = trade.trade(cplex, bidders, config)
    JsonWriter(config.resultFile).makeFile(ResultConverter.toJson(result))
    // 実験に使ったファイルを全て保存しておく

    Saver.run(bidders, result, config)
}
