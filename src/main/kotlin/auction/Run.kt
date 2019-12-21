package auction

import config.Config
import converter.BidderConverter
import cplex.Solver
import cplex.lpformat.Object
import impoter.JsonImporter
import impoter.LpImporter
import trade.average.AveProfitMax
import trade.padding_method.PaddingMethod
import winner.ProfitMaxDoubleAuction
import winner.ProfitMaxPaddingDoubleAuction
import writer.Saver

/**
 * $targetAuctionのオークションを，$bidDir/targetDataを$ite分繰り返して行い結果を出力する
 */
fun main(args: Array<String>) {
    val config = Config.fromJson("config")
    val lpFileName = "lp"
    var liar = 0
    config.targetAuction.forEach {
        println("$it start")
        // 入札作成~結果出力
        config.targetData.map { data ->
            // iteが存在していれば繰り返す
            config.targetDataIterate?.also { ite ->
                for (i in 0 until ite) {
                    val bidders = List(config.provider + config.requester) { j ->
                        BidderConverter.fromJson(JsonImporter("${config.bidDir}/$data/$i/bidder$j").getString())
                    }
                    val (lpMaker, trade) = when (it) {
                        "PaddingMethod" -> {
                            config.lpFile = lpFileName
                            Pair(ProfitMaxPaddingDoubleAuction(config = config, obj = Object.MAX, bidders = bidders), PaddingMethod)
                        }
                        "利益最大化-平均" -> {
                            config.lpFile = lpFileName
                            Pair(ProfitMaxDoubleAuction(config = config, obj = Object.MAX, bidders = bidders), AveProfitMax)
                        }
                        else -> {
                            throw Exception("Auction: ${it}は存在しません")
                        }
                    }

                    lpMaker.makeLpFile()
                    println("LPファイルの作成")
                    val start = System.currentTimeMillis();
                    val cplex = Solver(LpImporter("${config.lpDir}/${config.lpFile}").getCplex()).solve()
                    println("勝者決定問題")
                    val result = trade.run(cplex, bidders, config, start)
                    println("オークション終了")

                    Saver.run(bidders, result, config, it, data, i)
                }
                // iteが指定されなければ，繰り返さない
            } ?: run {
                throw Exception("ite is null")
            }
        }
    }
}
