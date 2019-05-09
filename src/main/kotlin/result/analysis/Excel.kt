package result.analysis

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
    // 場合分けの数
    val sheetNum = 8
    var min = 50.0
    var max = 150.0

    Excel.initExcel("Result/conclusion01", sheetNum)

    for (s in 0 until sheetNum) {
        var conclusionDir = "Result/supply-$min-$max"
        val auction = listOf(
                "利益最大化-取引価格-平均",
                "コスト最小化-ペナルティ-10000.0-平均",
                "提供単価最小化-ペナルティ-10000.0-利益率40%",
                "提供単価最小化-ペナルティ-10000.0-利益率60%")

        val con = (0..(auction.size - 1)).toList().map {
            ConclusionConverter.fromJson(JsonImporter("$conclusionDir/${auction[it]}").getString())
        }
        Excel.write(s, con)
        min += 50.0
        max += 50.0
    }

}

object Excel {
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
}