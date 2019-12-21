package auction

import config.Config
import converter.BidderConverter
import converter.ResultConverter
import impoter.JsonImporter
import trade.average.AveProfitMax
import trade.padding_method.PaddingMethod
import writer.Saver

/**
 * 一度Cplexで解は出したが結果の出力をやり直したいときのプログラム
 *
 */
fun main(args: Array<String>) {
    val config = Config.fromJson("config")
    val lpFileName = "lp"
    config.targetAuction.forEach {
        println("$it start")
        // 入札作成~結果出力
        config.targetData.map { data ->
            // iteが存在していれば繰り返す
            config.targetDataIterate?.also { ite ->
                for (i in 0 until ite) {
                    val bidders = List(config.provider + config.requester) { j ->
                        BidderConverter.fromJson(JsonImporter("${config.resultDir}/$data/$i/$it/Bid/bidder$j").getString())
                    }
                    val trade = when (it) {
                        "PaddingMethod" -> {
                            config.lpFile = lpFileName
                            PaddingMethod
                        }
                        "利益最大化-平均" -> {
                            config.lpFile = lpFileName
                            AveProfitMax
                        }
                        else -> {
                            throw Exception("Auction: ${it}は存在しません")
                        }
                    }

                    val rs = ResultConverter.fromJson(JsonImporter("${config.resultDir}/$data/$i/$it/result").getString())
                    val start = System.currentTimeMillis();
                    val result = trade.run(rs.x, rs.objectValue, bidders, config, start)

                    Saver.run(bidders, result, config, it)
                }
                // iteが指定されなければ，繰り返さない
            } ?: run {
                throw Exception("ite is null")
            }
        }
    }
}
