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
import winner.ProfitMaxDoubleAuction
import winner.ProfitMaxPaddingDoubleAuction
import writer.Saver

/**
 * $targetAuctionのオークションを，$bidDir/targetDataを$ite分繰り返して行い結果を出力する
 * 提供単価最小化に関しては，自動で$targetDataを$targetData-${profitRate}%と変換する
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
            val con = when {
                data.contains("liar") -> {
//                    liar++
                    println("data ${data}")
                    println("liar=$liar")
                    config.copy(lieProviderNumber = liar)
                }
                else -> {
                    config
                }
            }
            config.targetDataIterate?.also { ite ->
                for (i in 0 until ite) {
                    val bidders = List(config.provider + config.requester) { j ->
                        BidderConverter.fromJson(JsonImporter("${config.bidDir}/$data/$i/bidder$j").getString())
                    }
                    val (lpMaker, trade) = when (it) {
                        "PaddingMethod" -> {
                            con.lpFile = lpFileName
                            Pair(ProfitMaxPaddingDoubleAuction(config = con, obj = Object.MAX, bidders = bidders), PaddingMethod)
                        }
                        "利益最大化-平均" -> {
                            con.lpFile = lpFileName
                            Pair(ProfitMaxDoubleAuction(config = con, obj = Object.MAX, bidders = bidders), AveProfitMax)
                        }
                        "コスト最小化-ペナルティ-10000.0-平均" -> {
                            con.lpFile = lpFileName
                            con.penalty = 10000.0
                            Pair(CostMinPenaltyAuction(config = con, obj = Object.MAX, bidders = bidders), AvePenaltyCostMin)
                        }
                        "提供単価最小化-ペナルティ-10000.0-profit10%" -> {
                            con.lpFile = lpFileName
                            con.profitRate = 10
                            // TODO 微妙なきがする
                            con.targetData = config.targetData.map { t ->
                                if (t.contains("%")) {
                                    t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                                } else {
                                    "$t-profit${con.profitRate}%"
                                }
                            }
                            con.penalty = 10000.0
                            Pair(CostMinPenaltyAuction(config = con, obj = Object.MAX, bidders = bidders), SingleCostMin)
                        }
                        "提供単価最小化-ペナルティ-10000.0-profit20%" -> {
                            con.lpFile = lpFileName
                            con.profitRate = 20
                            con.targetData = con.targetData.map { t ->
                                if (t.contains("%")) {
                                    t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                                } else {
                                    "$t-profit${config.profitRate}%"
                                }
                            }
                            config.penalty = 10000.0
                            Pair(CostMinPenaltyAuction(config = con, obj = Object.MAX, bidders = bidders), SingleCostMin)
                        }
                        "提供単価最小化-ペナルティ-10000.0-profit30%" -> {
                            con.lpFile = lpFileName
                            con.profitRate = 30
                            con.targetData = con.targetData.map { t ->
                                if (t.contains("%")) {
                                    t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                                } else {
                                    "$t-profit${con.profitRate}%"
                                }
                            }
                            config.penalty = 10000.0
                            Pair(CostMinPenaltyAuction(config = con, obj = Object.MAX, bidders = bidders), SingleCostMin)
                        }
                        "提供単価最小化-ペナルティ-10000.0-profit40%" -> {
                            con.lpFile = lpFileName
                            con.profitRate = 40
                            con.targetData = con.targetData.map { t ->
                                if (t.contains("%")) {
                                    t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                                } else {
                                    "$t-profit${con.profitRate}%"
                                }
                            }
                            con.penalty = 10000.0
                            Pair(CostMinPenaltyAuction(config = con, obj = Object.MAX, bidders = bidders), SingleCostMin)
                        }
                        "提供単価最小化-ペナルティ-10000.0-profit50%" -> {
                            con.lpFile = lpFileName
                            con.profitRate = 50
                            con.targetData = con.targetData.map { t ->
                                if (t.contains("%")) {
                                    t.replace(Regex("profit\\d*%"), "profit${con.profitRate}%")
                                } else {
                                    "$t-profit${config.profitRate}%"
                                }
                            }
                            con.penalty = 10000.0
                            Pair(CostMinPenaltyAuction(config = con, obj = Object.MAX, bidders = bidders), SingleCostMin)
                        }
                        "提供単価最小化-ペナルティ-10000.0-profit60%" -> {
                            con.lpFile = lpFileName
                            con.profitRate = 60
                            con.targetData = config.targetData.map { t ->
                                if (t.contains("%")) {
                                    t.replace(Regex("profit\\d*%"), "profit${con.profitRate}%")
                                } else {
                                    "$t-profit${config.profitRate}%"
                                }
                            }
                            con.penalty = 10000.0
                            Pair(CostMinPenaltyAuction(config = con, obj = Object.MAX, bidders = bidders), SingleCostMin)
                        }
                        else -> {
                            throw Exception("Auction: ${it}は存在しません")
                        }
                    }

                    lpMaker.makeLpFile()
                    println("LPファイルの作成")
                    val start = System.currentTimeMillis();
                    val cplex = Solver(LpImporter("${con.lpDir}/${con.lpFile}").getCplex()).solve()
                    println("勝者決定問題")
                    val result = trade.run(cplex, bidders, con, start)
                    println("オークション終了")

                    // TODO: ここでやること？
                    if (data.contains("%")) {
                        con.resultFile = "${data.replace(Regex("-profit\\d*%"), "")}/$i/$it"
                    } else {
                        con.resultFile = "$data/$i/$it"
                    }

                    Saver.run(bidders, result, con)
                }
                // iteが指定されなければ，繰り返さない
            } ?: run {
                throw Exception("ite is null")
            }
        }
    }
}
