@file:Suppress("LocalVariableName")

package result.analysis

import config.Config
import converter.ConclusionConverter
import impoter.JsonImporter
import link.webarata3.kexcelapi.KExcel
import link.webarata3.kexcelapi.get
import link.webarata3.kexcelapi.set
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream


/*
* auction = 0 総利益最大化
* auction = 1 コスト最小化
* auction = 2 提供単価最小化 40%
* auction = 3 提供単価最小化 50%
* auction = 4 提供単価最小化 60%
* */

fun main(args: Array<String>) {
    val config = Config.fromJson("config-test")

    //$resultDir/$dataset/$auction.json

    val conList = config.targetData.map { data ->
        config.targetAuction.map { auction ->
            ConclusionConverter.fromJson(JsonImporter("${config.resultDir}/$data/$auction").getString())
        }
    }
    Excel.outConclusionEachSheetDataset("test-1-excel", config, conList)

    Excel.outConclusionEachSheetItem("test-2-excel", config, conList)


//    // 場合分けの数
//    val sheetNum = 8
//    var min = 50.0
//    var max = 150.0
//
//    Excel.initExcel("Result/conclusion01", sheetNum)
//
//    for (s in 0 until sheetNum) {
//        var conclusionDir = "Result/supply-$min-$max"
//        val auction = listOf(
//                "利益最大化-取引価格-平均",
//                "コスト最小化-ペナルティ-10000.0-平均",
//                "提供単価最小化-ペナルティ-10000.0-利益率40%",
//                "提供単価最小化-ペナルティ-10000.0-利益率60%")
//
//        val con = (0 until auction.size).toList().map {
//            ConclusionConverter.fromJson(JsonImporter("$conclusionDir/${auction[it]}").getString())
//        }
//        Excel.write(s, con)
//        min += 50.0
//        max += 50.0
//    }
}

object Excel {
    private const val SUM_PROFIT = "総利益"
    private const val SUM_COST = "総コスト"
    private const val PROVIDER_PROFIT_AVE = "提供企業側の利益の平均"
    private const val WIN_BID_NUMBER = "勝者となった要求数"
    private const val PROVIDEER_RATE = "提供率"
    private const val TRADE_PRICE = "取引価格"
    private const val BEFORE_PROVIDER_AVAILABILITY_RATIO = "取引前稼働率"
    private const val AFTER_PROVIDER_AVAILABILITY_RATIO = "取引後稼働率"

    private val itemList = listOf(
            SUM_PROFIT,
            SUM_COST,
            PROVIDER_PROFIT_AVE,
            WIN_BID_NUMBER,
            PROVIDEER_RATE,
            TRADE_PRICE,
            BEFORE_PROVIDER_AVAILABILITY_RATIO,
            AFTER_PROVIDER_AVAILABILITY_RATIO)

    private val AVE = "AVE."
    private val SD = "S.D."


    /**
     *
     * |      |            | Auction1 | Auction2 |
     * | ITEM | AVE and SD | VALUE    | VALUE    |
     *
     * sheet datasetA, datasetB..
     * @param file
     * @param config
     */
    fun outConclusionEachSheetDataset(file: String, config: Config, conList: List<List<Conclusion>>) {
        val AUCTION_ROW = 0
        val SUM_PROFIT_AVE_ROW = 1
        val SUM_PROFIT_SD_ROW = 2
        val SUM_COST_ROW_AVE = 3
        val SUM_COST_ROW_SD = 4
        val PROVIDER_PROFIT_AVE_AVE_ROW = 5
        val PROVIDER_PROFIT_AVE_SD_ROW = 6
        val WIN_BID_NUMBER_AVE_ROW = 7
        val WIN_BID_NUMBER_SD_ROW = 8
        val PROVIDER_RATE_AVE_ROW = 9
        val PROVIDER_RATE_SD_ROW = 10
        val TRADE_PRICE_AVE_ROW = 11
        val TRADE_PRICE_SD_ROW = 12
        val BEFORE_PROVIDER_AVAILABILITY_RATIO_AVE_ROW = 13
        val BEFORE_PROVIDER_AVAILABILITY_RATIO_SD_ROW = 14
        val AFTER_PROVIDER_AVAILABILITY_RATIO_AVE_ROW = 15
        val AFTER_PROVIDER_AVAILABILITY_RATIO_SD_ROW = 16
        val ITEM_COLUMUN = 0
        val AVE_AND_SD_COLUMN = 1

        initExcel(file, config.targetData)
        KExcel.open("$file.xlsx").use { workbook ->
            config.targetData.forEachIndexed { index, it ->
                val sheet = workbook[index]

                sheet[ITEM_COLUMUN, SUM_PROFIT_AVE_ROW] = SUM_PROFIT
                sheet[AVE_AND_SD_COLUMN, SUM_PROFIT_AVE_ROW] = AVE
                sheet[AVE_AND_SD_COLUMN, SUM_PROFIT_SD_ROW] = SD
                sheet[ITEM_COLUMUN, SUM_COST_ROW_AVE] = SUM_COST
                sheet[AVE_AND_SD_COLUMN, SUM_COST_ROW_AVE] = AVE
                sheet[AVE_AND_SD_COLUMN, SUM_COST_ROW_SD] = SD
                sheet[ITEM_COLUMUN, PROVIDER_PROFIT_AVE_AVE_ROW] = PROVIDER_PROFIT_AVE
                sheet[AVE_AND_SD_COLUMN, PROVIDER_PROFIT_AVE_AVE_ROW] = AVE
                sheet[AVE_AND_SD_COLUMN, PROVIDER_PROFIT_AVE_SD_ROW] = SD
                sheet[ITEM_COLUMUN, WIN_BID_NUMBER_AVE_ROW] = WIN_BID_NUMBER
                sheet[AVE_AND_SD_COLUMN, WIN_BID_NUMBER_AVE_ROW] = AVE
                sheet[AVE_AND_SD_COLUMN, WIN_BID_NUMBER_SD_ROW] = SD
                sheet[ITEM_COLUMUN, PROVIDER_RATE_AVE_ROW] = PROVIDEER_RATE
                sheet[AVE_AND_SD_COLUMN, PROVIDER_RATE_AVE_ROW] = AVE
                sheet[AVE_AND_SD_COLUMN, PROVIDER_RATE_SD_ROW] = SD
                sheet[ITEM_COLUMUN, TRADE_PRICE_AVE_ROW] = TRADE_PRICE
                sheet[AVE_AND_SD_COLUMN, TRADE_PRICE_AVE_ROW] = AVE
                sheet[AVE_AND_SD_COLUMN, TRADE_PRICE_SD_ROW] = SD
                sheet[ITEM_COLUMUN, BEFORE_PROVIDER_AVAILABILITY_RATIO_AVE_ROW] = BEFORE_PROVIDER_AVAILABILITY_RATIO
                sheet[AVE_AND_SD_COLUMN, BEFORE_PROVIDER_AVAILABILITY_RATIO_AVE_ROW] = AVE
                sheet[AVE_AND_SD_COLUMN, BEFORE_PROVIDER_AVAILABILITY_RATIO_SD_ROW] = SD
                sheet[ITEM_COLUMUN, AFTER_PROVIDER_AVAILABILITY_RATIO_AVE_ROW] = AFTER_PROVIDER_AVAILABILITY_RATIO
                sheet[AVE_AND_SD_COLUMN, AFTER_PROVIDER_AVAILABILITY_RATIO_AVE_ROW] = AVE
                sheet[AVE_AND_SD_COLUMN, AFTER_PROVIDER_AVAILABILITY_RATIO_SD_ROW] = SD

                config.targetAuction.forEachIndexed { auctionIndex, auction ->
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, AUCTION_ROW] = auction
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, SUM_PROFIT_AVE_ROW] = conList[index][auctionIndex].sumProfitAve
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, SUM_PROFIT_SD_ROW] = conList[index][auctionIndex].sumProfitSD
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, SUM_COST_ROW_AVE] = conList[index][auctionIndex].sumCostAve
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, SUM_COST_ROW_SD] = conList[index][auctionIndex].sumCostSD
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, PROVIDER_PROFIT_AVE_AVE_ROW] = conList[index][auctionIndex].providerProfitAve
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, PROVIDER_PROFIT_AVE_SD_ROW] = conList[index][auctionIndex].providerProfitSD
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, WIN_BID_NUMBER_AVE_ROW] = conList[index][auctionIndex].winBidAve
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, WIN_BID_NUMBER_SD_ROW] = conList[index][auctionIndex].winBidSD
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, PROVIDER_RATE_AVE_ROW] = conList[index][auctionIndex].providerTimeRatioAve
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, PROVIDER_RATE_SD_ROW] = conList[index][auctionIndex].providerTimeRatioSD
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, TRADE_PRICE_AVE_ROW] = conList[index][auctionIndex].tradeAve
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, TRADE_PRICE_SD_ROW] = conList[index][auctionIndex].tradeSD
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, BEFORE_PROVIDER_AVAILABILITY_RATIO_AVE_ROW] = conList[index][auctionIndex].providerBeforeAvailabilityRatioAve
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, BEFORE_PROVIDER_AVAILABILITY_RATIO_SD_ROW] = conList[index][auctionIndex].providerBeforeAvailabilityRatioSD
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, AFTER_PROVIDER_AVAILABILITY_RATIO_AVE_ROW] = conList[index][auctionIndex].providerAfterAvailabilityRatioAve
                    sheet[AVE_AND_SD_COLUMN + 1 + auctionIndex, AFTER_PROVIDER_AVAILABILITY_RATIO_SD_ROW] = conList[index][auctionIndex].providerAfterAvailabilityRatioSD
                }
            }
            KExcel.write(workbook, "${file}.xlsx")
        }
    }

    /**
     * | Ave.    | Auctin1 | Auction2 |
     * | [a, b]  | value   | value
     * | [c, d]  | value   | value
     *
     *
     *
     * | S.D.    | Auctin1 | Auction2 |
     * | [a, b]  | value   | value
     * | [c, d]  | value   | value
     *
     * sheet itemA,  itemB...
     * @param file
     * @param config
     * @param conList (実験条件, オークション)の形
     */
    fun outConclusionEachSheetItem(file: String, config: Config, conList: List<List<Conclusion>>) {
        val AUCTION_ROW = 0
        val DATA_COLUMN = 0
        initExcel(file, itemList)

        KExcel.open("$file.xlsx").use { workbook ->
            itemList.forEachIndexed { itemIndex, item ->
                val sheet = workbook[itemIndex]

                sheet[DATA_COLUMN, AUCTION_ROW] = AVE
                // オークション名の初期化
                config.targetAuction.forEachIndexed { i, a ->
                    sheet[DATA_COLUMN + 1 + i, AUCTION_ROW] = a
                }

                // 平均値の値
                config.targetData.forEachIndexed { dataIndex, data ->
                    val d = data.split("-")
                    sheet[DATA_COLUMN, AUCTION_ROW + 1 + dataIndex] = "[${d[1]}, ${d[2]}]"
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
                    sheet[DATA_COLUMN, SD_ROW + AUCTION_ROW + 1 + dataIndex] = "[${d[1]}, ${d[2]}]"
                    config.targetAuction.forEachIndexed { auctionIndex, auction ->
                        sheet[DATA_COLUMN + 1 + auctionIndex, SD_ROW + AUCTION_ROW + 1 + dataIndex] = conList[dataIndex][auctionIndex].getValue(item, SD)
                    }
                }
            }
            KExcel.write(workbook, "${file}.xlsx")
        }
    }


    fun write(sheetNum: Int, con: List<Conclusion>) {
        val resultExcelFile = "Result/conclusion01"

        KExcel.open("${resultExcelFile}.xlsx").use { workbook ->
            val sheet = workbook[sheetNum]
            sheet[0, 2] = "総利益"
            sheet[0, 4] = "総コスト"
            sheet[0, 6] = "提供企業側の利益の平均"
            sheet[0, 8] = "要求企業側の利益の平均"
            sheet[0, 10] = "勝者となった要求数"
            sheet[0, 12] = "提供率"
            sheet[0, 14] = "取引価格"

            sheet[1, 2] = "Ave."
            sheet[1, 3] = "S.D."
            sheet[1, 4] = "Ave."
            sheet[1, 5] = "S.D."
            sheet[1, 6] = "Ave."
            sheet[1, 7] = "S.D."
            sheet[1, 8] = "Ave."
            sheet[1, 9] = "S.D."
            sheet[1, 10] = "Ave."
            sheet[1, 11] = "S.D."
            sheet[1, 12] = "Ave."
            sheet[1, 13] = "S.D."

            sheet[2, 0] = "手法I"
            sheet[3, 0] = "手法II"
            sheet[4, 0] = "手法III"
            sheet[4, 1] = "40%"
            sheet[5, 1] = "60%"
            con.forEachIndexed { index, it ->
                writeConclusion(sheet, it, index)
            }
            KExcel.write(workbook, "${resultExcelFile}.xlsx")
        }


    }

    fun writeConclusion(sheet: Sheet, con: Conclusion, auction: Int) {
        println("auction $auction")
        sheet[2 + auction, 2] = con.sumProfitAve
        sheet[2 + auction, 3] = con.sumProfitSD
        sheet[2 + auction, 4] = con.sumCostAve
        sheet[2 + auction, 5] = con.sumCostSD
        sheet[2 + auction, 6] = con.providerProfitAve
        sheet[2 + auction, 7] = con.providerProfitSD
        sheet[2 + auction, 8] = con.requesterProfitAve
        sheet[2 + auction, 9] = con.requesterProfitSD
        sheet[2 + auction, 10] = con.winBidAve
        sheet[2 + auction, 11] = con.winBidSD
        sheet[2 + auction, 12] = con.providerTimeRatioAve
        sheet[2 + auction, 13] = con.providerTimeRatioSD
        sheet[2 + auction, 14] = con.tradeAve
        sheet[2 + auction, 15] = con.tradeSD
    }

    fun initExcel(fileName: String, sheetNum: Int) {
        val wb = XSSFWorkbook()
        val out = FileOutputStream("${fileName}.xlsx")
        for (i in 0 until sheetNum) {
            wb.createSheet()
        }
        wb.write(out)
        out.close()
    }

    /**
     * 作成するexcelのファイル名と作成したsheet名のリストを受け取る
     *
     * @param fileName
     * @param sheetNames
     */
    fun initExcel(fileName: String, sheetNames: List<String>) {
        val wb = XSSFWorkbook()
        val out = FileOutputStream("${fileName}.xlsx")
        sheetNames.forEach {
            wb.createSheet(it)
        }
        wb.write(out)
        out.close()
    }

    fun Conclusion.getValue(item: String, kind: String): Double = when (val str = "$item-$kind") {
        "$SUM_PROFIT-$AVE" -> this.sumProfitAve
        "$SUM_PROFIT-$SD" -> this.sumProfitSD
        "$SUM_COST-$AVE" -> this.sumCostAve
        "$SUM_COST-$SD" -> this.sumCostSD
        "$PROVIDER_PROFIT_AVE-$AVE" -> this.providerProfitAve
        "$PROVIDER_PROFIT_AVE-$SD" -> this.providerProfitSD
        "$WIN_BID_NUMBER-$AVE" -> this.winBidAve
        "$WIN_BID_NUMBER-$SD" -> this.winBidSD
        "$PROVIDEER_RATE-$AVE" -> this.providerTimeRatioAve
        "$PROVIDEER_RATE-$SD" -> this.providerTimeRatioSD
        "$TRADE_PRICE-$AVE" -> this.tradeAve
        "$TRADE_PRICE-$SD" -> this.tradeSD
        "$BEFORE_PROVIDER_AVAILABILITY_RATIO-$AVE" -> this.providerBeforeAvailabilityRatioAve
        "$BEFORE_PROVIDER_AVAILABILITY_RATIO-$SD" -> this.providerBeforeAvailabilityRatioSD
        "$AFTER_PROVIDER_AVAILABILITY_RATIO-$AVE" -> this.providerAfterAvailabilityRatioAve
        "$AFTER_PROVIDER_AVAILABILITY_RATIO-$SD" -> this.providerAfterAvailabilityRatioSD
        else -> {
            throw Exception("$str は存在しません")
        }
    }
}