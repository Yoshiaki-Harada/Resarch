package auction

import config.Config
import converter.BidderConverter
import cplex.Solver
import cplex.lpformat.Object
import impoter.JsonImporter
import impoter.LpImporter
import trade.average.AveCostMin
import trade.average.AvePenaltyCostMin
import trade.average.AveProfitMax
import trade.provider_single.SingleCostMin
import winner.CostMinPenaltyAuction
import winner.CostMinProviderAuction
import winner.ProfitMaxDoubleAuction
import writer.Saver

/**
 * targetAuctionのオークションを，$targetDataと$ite分繰り返して行い結果を出力する
 * 提供単価最小化に関しては，自動で$targetDataを$targetData-${profitRate}%と変換する
 */
fun main(args: Array<String>) {
    val config = Config.fromJson("config")
    config.targetAuction.forEach {
        val (lpMaker, obj, trade) = when (it) {
            "VCG" -> {
                throw Exception("Auction: ${it}は存在しません")
            }
            "コスト最小化" -> {
                config.lpFile = it
                Triple(CostMinProviderAuction, Object.MIN, AveCostMin)
            }
            "利益最大化-平均" -> {
                config.lpFile = it
                Triple(ProfitMaxDoubleAuction, Object.MAX, AveProfitMax)
            }
            "コスト最小化-ペナルティ-10000.0-平均" -> {
                config.lpFile = it
                config.penalty = 10000.0
                Triple(CostMinPenaltyAuction, Object.MIN, AvePenaltyCostMin)
            }
            "提供単価最小化-ペナルティ-10000.0-profit10%" -> {
                config.lpFile = it
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
                Triple(CostMinPenaltyAuction, Object.MIN, SingleCostMin)
            }
            "提供単価最小化-ペナルティ-10000.0-profit20%" -> {
                config.lpFile = it
                config.profitRate = 20
                config.targetData = config.targetData.map { t ->
                    if (t.contains("%")) {
                        t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                    } else {
                        "$t-profit${config.profitRate}%"
                    }
                }
                config.penalty = 10000.0
                Triple(CostMinPenaltyAuction, Object.MIN, SingleCostMin)
            }
            "提供単価最小化-ペナルティ-10000.0-profit30%" -> {
                config.lpFile = it
                config.profitRate = 30
                config.targetData = config.targetData.map { t ->
                    if (t.contains("%")) {
                        t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                    } else {
                        "$t-profit${config.profitRate}%"
                    }
                }
                config.penalty = 10000.0
                Triple(CostMinPenaltyAuction, Object.MIN, SingleCostMin)
            }
            "提供単価最小化-ペナルティ-10000.0-profit40%" -> {
                config.lpFile = it
                config.profitRate = 40
                config.targetData = config.targetData.map { t ->
                    if (t.contains("%")) {
                        t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                    } else {
                        "$t-profit${config.profitRate}%"
                    }
                }
                config.penalty = 10000.0
                Triple(CostMinPenaltyAuction, Object.MIN, SingleCostMin)
            }
            "提供単価最小化-ペナルティ-10000.0-profit50%" -> {
                config.lpFile = it
                config.profitRate = 50
                config.targetData = config.targetData.map { t ->
                    if (t.contains("%")) {
                        t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                    } else {
                        "$t-profit${config.profitRate}%"
                    }
                }
                config.penalty = 10000.0
                Triple(CostMinPenaltyAuction, Object.MIN, SingleCostMin)
            }
            "提供単価最小化-ペナルティ-10000.0-profit60%" -> {
                config.lpFile = it
                config.profitRate = 60
                config.targetData = config.targetData.map { t ->
                    if (t.contains("%")) {
                        t.replace(Regex("profit\\d*%"), "profit${config.profitRate}%")
                    } else {
                        "$t-profit${config.profitRate}%"
                    }
                }
                config.penalty = 10000.0
                Triple(CostMinPenaltyAuction, Object.MIN, SingleCostMin)
            }
            else -> {
                throw Exception("Auction: ${it}は存在しません")
            }
        }

        // 入札作成~結果出力
        config.targetData.map { data ->
            // iteが存在していれば繰り返す
            config.targetDataIterate?.also { ite ->
                for (i in 0 until ite) {
                    val bidders = List(config.provider + config.requester) { j ->
                        BidderConverter.fromJson(JsonImporter("${config.bidDir}/$data/$i/bidder$j").getString())
                    }
                    lpMaker.makeLpFile(config, obj, bidders)
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
                val bidders = List(config.provider + config.requester) { i ->
                    BidderConverter.fromJson(JsonImporter("${config.bidDir}/$data/bidder$i").getString())
                }
                lpMaker.makeLpFile(config, obj, bidders)
                val cplex = Solver(LpImporter("${config.lpDir}/${config.lpFile}").getCplex()).solve()
                val result = trade.run(cplex, bidders, config)
                // TODO: ここでやること？
                if (data.contains("%")) {
                    config.resultFile = "${data.replace(Regex("-profit\\d*%"), "")}/$it"
                } else {
                    config.resultFile = "$data/$it"
                }
                Saver.run(bidders, result, config)
            }
        }
    }
}
