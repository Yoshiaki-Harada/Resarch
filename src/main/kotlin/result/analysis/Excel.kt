@file:Suppress("LocalVariableName")

package result.analysis

import config.Config
import converter.ConclusionConverter
import impoter.JsonImporter
import link.webarata3.kexcelapi.KExcel
import link.webarata3.kexcelapi.get
import link.webarata3.kexcelapi.set
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream


/**
 * Excelに結果を出力する為のmain関数
 * targetAuctionとtargetDataのConclusionファイルを読み込んで，Excelに出力
 * @param args
 */
fun main(args: Array<String>) {
    val config = Config.fromJson("config")

    val conList = config.targetData.map { data ->
        val dirName = "${config.resultDir}/${config.bidDir.replace("Bid", "")}/$data"
        config.targetAuction.map { auction ->
            ConclusionConverter.fromJson(JsonImporter("$dirName/$auction").getString())
        }
    }
    Excel.outConclusionEachSheetDataset("${config.resultDir}/result-sheet-dataset", config, conList)

    Excel.outConclusionEachSheetItem("${config.resultDir}/result-sheet-item", config, conList)

    config.targetAuction.forEachIndexed { index, s ->
        Excel.outConclusionEachSheetItemForMasterThesis("${config.resultDir}/result-characteristic-$s", config, conList, index)
    }
}

/**
 * 結果をExcelに出力する為のオブジェクト
 */
object Excel {
    /**
     * 評価指標
     */
    private const val SUM_PROFIT = "提供側と要求側の総利益"
    private const val SUM_PROFIT_INCLUDE_AUCTIONEER = "総利益(オークション主催者込み)"
    private const val SUM_PROVIDER_PROFIT = "総提供企業利益"
    private const val SUM_REQUESTER_PROFIT = "総要求企業利益"
    private const val PROVIDER_PROFIT_AVE = "提供企業側の利益の平均"
    private const val REQUESTER_PROFIT_AVE = "要求企業側の利益の平均"
    private const val WIN_BID_NUMBER = "勝者となった要求の割合"
    private const val PROVIDER_RATE = "提供率"
    private const val PROVIDER_REVENUE = "収入"
    private const val PROVIDER_REVENUE_SUM = "総収入額"
    private const val PROVIDER_REVENUE_DENSITY = "収入(1Ts)"
    private const val REQUESTER_PAY = "支払い"
    private const val REQUESTER_PAY_SUM = "総支払い額"
    private const val BEFORE_PROVIDER_AVAILABILITY_RATIO = "取引前稼働率"
    private const val AFTER_PROVIDER_AVAILABILITY_RATIO = "取引後稼働率"
    private const val CHANGE_PROVIDER_AVAILABILITY_RATIO = "稼働率の変化率"
    private const val AUCTIONEER_PROFIT = "主催者の利益"
    private const val LIE_PROVIDERS_PROFIT = "虚偽申告-提供企業群の利益の平均"
    private const val LIE_PROVIDER_PROFIT = "虚偽申告提供企業の利益"
    private const val LIE_REQUESTER_PROFIT = "虚偽申告要求企業の利益"
    private const val LIE_PROVIDER_REVENUE_DENSITY = "虚偽申告-収入(1Ts)"
    private const val SURPLUS_PROFIT = "余剰利益"
    private const val AVE = "AVE."
    private const val SD = "S.D."

    /**
     *
     * |       |     | Auction1 | Auction2 |
     * | ITEM1 | AVE | VALUE    | VALUE    |
     * |       | SD  | VALUE    | VALUE    |
     * | ITEM2 | AVE | VALUE    | VALUE    |
     * |       | SD  | VALUE    | VALUE    |
     * sheet datasetA, datasetB..
     * @param file
     * @param config
     */
    fun outConclusionEachSheetDataset(file: String, config: Config, conList: List<List<Conclusion>>) {
        val AUCTION_ROW = 0
        val ITEM_COLUMUN = 0
        val AVE_AND_SD_COLUMN = 1

        initExcel(file, config.targetData)
        KExcel.open("$file.xlsx").use { workbook ->
            config.targetData.forEachIndexed { index, it ->
                val sheet = workbook[index]

                config.targetAuction.forEachIndexed { i, a ->
                    sheet[AVE_AND_SD_COLUMN + 1 + i, AUCTION_ROW] = a

                }

                config.items.forEachIndexed { itemIndex, item ->
                    sheet[ITEM_COLUMUN, 1 + 2 * itemIndex] = item
                    sheet[AVE_AND_SD_COLUMN, 1 + 2 * itemIndex] = AVE
                    sheet[AVE_AND_SD_COLUMN, 1 + 2 * itemIndex + 1] = SD
                    config.targetAuction.forEachIndexed { auctionIndex, auction ->
                        sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, 1 + 2 * itemIndex] = conList[index][auctionIndex].getValue(item, AVE)
                        sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, 1 + 2 * itemIndex + 1] = conList[index][auctionIndex].getValue(item, SD)
                    }
                }
            }
            KExcel.write(workbook, "${file}.xlsx")
        }
    }

    /**
     * | Ave.| Auctin1 | Auction2 |
     * |  a  | VALUE   | VALUE
     * |  b  | VALUE   | VALUE
     *
     *
     *
     * | S.D.| Auctin1 | Auction2 |
     * |  a  | VALUE   | VALUE
     * |  b  | VALUE   | VALUE
     *
     * sheet itemA,  itemB...
     * @param file
     * @param config
     * @param conList (実験条件, オークション)の形
     */
    fun outConclusionEachSheetItem(file: String, config: Config, conList: List<List<Conclusion>>) {
        val AUCTION_ROW = 0
        val DATA_COLUMN = 0
        initExcel(file, config.items)

        KExcel.open("$file.xlsx").use { workbook ->
            config.items.forEachIndexed { itemIndex, item ->
                val sheet = workbook[itemIndex]

                sheet[DATA_COLUMN, AUCTION_ROW] = AVE
                // オークション名の初期化
                config.targetAuction.forEachIndexed { i, a ->
                    sheet[DATA_COLUMN + 1 + i, AUCTION_ROW] = a
                }

                // 平均値の値
                config.targetData.forEachIndexed { dataIndex, data ->
                    sheet[DATA_COLUMN, AUCTION_ROW + 1 + dataIndex] = data
                    config.targetAuction.forEachIndexed { auctionIndex, auction ->
                        sheet[DATA_COLUMN + 1 + auctionIndex, AUCTION_ROW + 1 + dataIndex] = conList[dataIndex][auctionIndex].getValue(item, AVE)
                    }
                }

                // 標準偏差の値
                val SD_ROW = config.targetData.size + 3
                config.targetAuction.forEachIndexed { i, a ->
                    sheet[DATA_COLUMN + 1 + i, SD_ROW + AUCTION_ROW] = a
                }
                sheet[DATA_COLUMN, SD_ROW + AUCTION_ROW] = SD
                config.targetData.forEachIndexed { dataIndex, data ->
                    val d = data.split("-")
                    sheet[DATA_COLUMN, SD_ROW + AUCTION_ROW + 1 + dataIndex] = data
                    config.targetAuction.forEachIndexed { auctionIndex, auction ->
                        sheet[DATA_COLUMN + 1 + auctionIndex, SD_ROW + AUCTION_ROW + 1 + dataIndex] = conList[dataIndex][auctionIndex].getValue(item, SD)
                    }
                }
            }
            KExcel.write(workbook, "$file.xlsx")
        }
    }

    fun outConclusionEachSheetItemForMasterThesis(file: String, config: Config, conList: List<List<Conclusion>>, auctionIndex: Int) {
        val DATA_ROW = 0
        val AVE_ROW = DATA_ROW + 1
        val SD_ROW = AVE_ROW + 1
        val LABEL_COLUMN = 0
        initExcel(file, config.items)

        KExcel.open("$file.xlsx").use { workbook ->
            config.items.forEachIndexed { itemIndex, item ->
                val sheet = workbook[itemIndex]

                sheet[LABEL_COLUMN, AVE_ROW] = AVE
                sheet[LABEL_COLUMN, SD_ROW] = SD

                config.targetData.forEachIndexed { dataIndex, data ->
                    sheet[LABEL_COLUMN + 1 + dataIndex, DATA_ROW] = data
                    sheet[LABEL_COLUMN + 1 + dataIndex, AVE_ROW] = conList[dataIndex][auctionIndex].getValue(item, AVE)
                    sheet[LABEL_COLUMN + 1 + dataIndex, SD_ROW] = conList[dataIndex][auctionIndex].getValue(item, SD)
                }
            }
            KExcel.write(workbook, "$file.xlsx")
        }
    }

    /**
     * 作成するexcelのファイル名と作成するsheet名のリストを受け取る
     *
     * @param fileName
     * @param sheetNames
     */
    fun initExcel(fileName: String, sheetNames: List<String>) {
        val wb = XSSFWorkbook()
        val out = FileOutputStream("${fileName}.xlsx")
        sheetNames.forEach {
            wb.createSheet(it.replace("/", "-"))
//            wb.createSheet(it.split("/").last())
        }
        wb.write(out)
        out.close()
    }

    /**
     * ("評価指標", "平均or標準偏差")の形で値を取得する為の関数
     *
     * @param item
     * @param kind
     * @return
     */
    fun Conclusion.getValue(item: String, kind: String): Double = when (val str = "$item-$kind") {
        "$SUM_PROFIT-$AVE" -> this.sumProfitAve
        "$SUM_PROFIT-$SD" -> this.sumProfitSD
        "$SUM_PROVIDER_PROFIT-$AVE" -> this.sumProviderProfitAve
        "$SUM_PROVIDER_PROFIT-$SD" -> this.sumProviderProfitSD
        "$SUM_REQUESTER_PROFIT-$AVE" -> this.sumRequesterProfitAve
        "$SUM_REQUESTER_PROFIT-$SD" -> this.sumRequesterProfitSD
        "$SUM_PROFIT_INCLUDE_AUCTIONEER-$AVE" -> this.sumProfitIncludeAuctioneerAve
        "$SUM_PROFIT_INCLUDE_AUCTIONEER-$SD" -> this.sumProfitIncludeAuctioneerSD
        "$PROVIDER_PROFIT_AVE-$AVE" -> this.providerProfitAve
        "$PROVIDER_PROFIT_AVE-$SD" -> this.providerProfitSD
        "$REQUESTER_PROFIT_AVE-$AVE" -> this.requesterProfitAve
        "$REQUESTER_PROFIT_AVE-$SD" -> this.requesterProfitSD
        "$WIN_BID_NUMBER-$AVE" -> this.winBidAve
        "$WIN_BID_NUMBER-$SD" -> this.winBidSD
        "$PROVIDER_RATE-$AVE" -> this.providerTimeRatioAve
        "$PROVIDER_RATE-$SD" -> this.providerTimeRatioSD
        "$PROVIDER_REVENUE-$AVE" -> this.providerRevenueAve
        "$PROVIDER_REVENUE-$SD" -> this.providerRevenueSD
        "$PROVIDER_REVENUE_DENSITY-$AVE" -> this.providerRevenueDensityAve
        "$PROVIDER_REVENUE_DENSITY-$SD" -> this.providerRevenueDensitySD
        "$REQUESTER_PAY-$AVE" -> this.requesterPayAve
        "$REQUESTER_PAY-$SD" -> this.requesterPaySD
        "$BEFORE_PROVIDER_AVAILABILITY_RATIO-$AVE" -> this.providerBeforeAvailabilityRatioAve
        "$BEFORE_PROVIDER_AVAILABILITY_RATIO-$SD" -> this.providerBeforeAvailabilityRatioSD
        "$AFTER_PROVIDER_AVAILABILITY_RATIO-$AVE" -> this.providerAfterAvailabilityRatioAve
        "$AFTER_PROVIDER_AVAILABILITY_RATIO-$SD" -> this.providerAfterAvailabilityRatioSD
        "$PROVIDER_REVENUE_SUM-$AVE" -> this.sumRevenueAve
        "$PROVIDER_REVENUE_SUM-$SD" -> this.sumRevenueSD
        "$REQUESTER_PAY_SUM-$AVE" -> this.sumPayAve
        "$REQUESTER_PAY_SUM-$SD" -> this.sumPaySD
        "$AUCTIONEER_PROFIT-$AVE" -> this.auctioneerProfitAve
        "$AUCTIONEER_PROFIT-$SD" -> this.auctioneerProfitSD
        "$LIE_PROVIDERS_PROFIT-$AVE" -> this.liarConclusion?.providersProfitAve ?: 0.0
        "$LIE_PROVIDERS_PROFIT-$SD" -> this.liarConclusion?.providersProfitSD ?: 0.0
        "$LIE_PROVIDER_PROFIT-$AVE" -> this.liarConclusion?.providerProfitAve ?: 0.0
        "$LIE_PROVIDER_PROFIT-$SD" -> this.liarConclusion?.providerProfitSD ?: 0.0
        "$LIE_REQUESTER_PROFIT-$AVE" -> this.liarConclusion?.requesterProfitAve ?: 0.0
        "$LIE_REQUESTER_PROFIT-$SD" -> this.liarConclusion?.requesterProfitSD ?: 0.0
        "$LIE_PROVIDER_REVENUE_DENSITY-$AVE" -> this.liarConclusion?.providersRevenueDensityAve ?: 0.0
        "$LIE_PROVIDER_REVENUE_DENSITY-$SD" -> this.liarConclusion?.providersRevenueDensitySD ?: 0.0
        "$SURPLUS_PROFIT-$AVE" -> this.surplusProfitAve
        "$SURPLUS_PROFIT-$SD" -> this.surplusProfitSD
        "$CHANGE_PROVIDER_AVAILABILITY_RATIO-$AVE" -> this.availabilityChangeAve
        "$CHANGE_PROVIDER_AVAILABILITY_RATIO-$SD" -> this.availabilityChangeSD
        else -> {
            throw Exception("$str は存在しません")
        }
    }
}