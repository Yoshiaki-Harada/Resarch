package auction

import config.Config
import converter.BidderConverter
import cplex.Solver
import cplex.lpformat.Object
import impoter.JsonImporter
import impoter.LpImporter
import trade.average.AvePenaltyCostMin
import trade.average.AveProfitMax
import trade.padding_method.PaddingMethod
import trade.provider_single.SingleCostMin
import winner.CostMinPenaltyAuction
import winner.CostMinProviderAuction
import winner.ProfitMaxDoubleAuction
import winner.ProfitMaxPaddingDoubleAuction
import writer.Saver

/**
 * targetAuctionのオークションを，$targetDataと$ite分繰り返して行い結果を出力する
 * 提供単価最小化に関しては，自動で$targetDataを$targetData-${profitRate}%と変換する
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
                        "コスト最小化-ペナルティ-10000.0-平均" -> {
                            config.lpFile = lpFileName
                            config.penalty = 10000.0
                            Pair(CostMinPenaltyAuction(config = config, obj = Object.MAX, bidders = bidders), AvePenaltyCostMin)
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
                            Pair(CostMinPenaltyAuction(config = config, obj = Object.MAX, bidders = bidders), SingleCostMin)
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
                            config.penalty = 10000.0
                            Pair(CostMinPenaltyAuction(config = config, obj = Object.MAX, bidders = bidders), SingleCostMin)
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
                            Pair(CostMinPenaltyAuction(config = config, obj = Object.MAX, bidders = bidders), SingleCostMin)
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
                            Pair(CostMinPenaltyAuction(config = config, obj = Object.MAX, bidders = bidders), SingleCostMin)
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
                            Pair(CostMinPenaltyAuction(config = config, obj = Object.MAX, bidders = bidders), SingleCostMin)
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
                            Pair(CostMinPenaltyAuction(config = config, obj = Object.MAX, bidders = bidders), SingleCostMin)
                        }
                        else -> {
                            throw Exception("Auction: ${it}は存在しません")
                        }
                    }

                    lpMaker.makeLpFile()
                    println("LPファイルの作成")
                    val cplex = Solver(LpImporter("${config.lpDir}/${config.lpFile}").getCplex()).solve()
                    println("勝者決定問題")
                    val result = trade.run(cplex, bidders, config)
                    println("オークション終了")

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
