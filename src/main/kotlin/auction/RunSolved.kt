package auction

import config.Config
import converter.BidderConverter
import converter.ResultConverter
import impoter.JsonImporter
import trade.average.AvePenaltyCostMin
import trade.average.AveProfitMax
import trade.padding_method.PaddingMethod
import trade.provider_single.SingleCostMin
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
                        "コスト最小化-ペナルティ-10000.0-平均" -> {
                            config.lpFile = lpFileName
                            config.penalty = 10000.0
                            AvePenaltyCostMin
                        }
                        "提供単価最小化-ペナルティ-10000.0-profit10%" -> {
                            config.lpFile = lpFileName
                            config.profitRate = 10
                            // TODO 微妙なきがする
                            config.targetData = config.targetData.map { t ->
                                if (t.contains("%")) {
                                    t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                                } else {
                                    "$t-profit${config.profitRate}%"
                                }
                            }
                            config.penalty = 10000.0
                            SingleCostMin
                        }
                        "提供単価最小化-ペナルティ-10000.0-profit20%" -> {
                            config.lpFile = lpFileName
                            config.profitRate = 20
                            config.targetData = config.targetData.map { t ->
                                if (t.contains("%")) {
                                    t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                                } else {
                                    "$t-profit${config.profitRate}%"
                                }
                            }
                            SingleCostMin
                        }
                        "提供単価最小化-ペナルティ-10000.0-profit30%" -> {
                            config.lpFile = lpFileName
                            config.profitRate = 30
                            config.targetData = config.targetData.map { t ->
                                if (t.contains("%")) {
                                    t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                                } else {
                                    "$t-profit${config.profitRate}%"
                                }
                            }
                            config.penalty = 10000.0
                            SingleCostMin
                        }
                        "提供単価最小化-ペナルティ-10000.0-profit40%" -> {
                            config.lpFile = lpFileName
                            config.profitRate = 40
                            config.targetData = config.targetData.map { t ->
                                if (t.contains("%")) {
                                    t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                                } else {
                                    "$t-profit${config.profitRate}%"
                                }
                            }
                            config.penalty = 10000.0
                            SingleCostMin
                        }
                        "提供単価最小化-ペナルティ-10000.0-profit50%" -> {
                            config.lpFile = lpFileName
                            config.profitRate = 50
                            config.targetData = config.targetData.map { t ->
                                if (t.contains("%")) {
                                    t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                                } else {
                                    "$t-profit${config.profitRate}%"
                                }
                            }
                            config.penalty = 10000.0
                            SingleCostMin
                        }
                        "提供単価最小化-ペナルティ-10000.0-profit60%" -> {
                            config.lpFile = lpFileName
                            config.profitRate = 60
                            config.targetData = config.targetData.map { t ->
                                if (t.contains("%")) {
                                    t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                                } else {
                                    "$t-profit${config.profitRate}%"
                                }
                            }
                            config.penalty = 10000.0
                            SingleCostMin
                        }
                        else -> {
                            throw Exception("Auction: ${it}は存在しません")
                        }
                    }

                    val rs = ResultConverter.fromJson(JsonImporter("${config.resultDir}/$data/$i/$it/result").getString())

                    val result = trade.run(rs.x, rs.objectValue, bidders, config)

                    // TODO: ここでやること？
                    if (data.contains("%")) {
                        config.resultFile = "${data.replace(Regex("-profit\\d*%"), "")}/$i/$it"
                    } else {
                        config.resultFile = "$data/$i/$it"
                    }

                    Saver.run(bidders, result, config)
                }
                // iteが指定されなければ，繰り返さない
            } ?: run {
                throw Exception("ite is null")
            }
        }
    }
}
