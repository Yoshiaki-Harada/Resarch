import converter.ResultConverter
import impoter.JsonImporter
import writer.JsonWriter

fun main() {
    for (i in 0..9) {
        println(i)
        val rs = ResultConverter.fromJson(JsonImporter("Result/Provider=30/supply-100.0-200.0/$i/PaddingMethod/result").getString())
        val newRs = rs.copy(auctioneerProfit = rs.sumPay - rs.sumRevenue)
        JsonWriter("Result/Provider=30/supply-100.0-200.0/$i/PaddingMethod/result").makeFile(ResultConverter.toJson(newRs))
    }
}