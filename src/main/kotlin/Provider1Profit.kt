import config.Config
import converter.ConclusionConverter
import converter.ResultConverter
import impoter.JsonImporter
import result.analysis.Conclusion
import result.analysis.LiarConclusion

/**
 * TODO 虚偽申告企業の結果出力
 *
 */
fun main() {
    val config = Config.fromJson("config")
    config.targetData.forEach { dataset ->
        if (config.targetDataIterate == null) {
            throw Exception("ite がnullです")
        }
        val dir = "${config.resultDir}/$dataset"

        val rs1 = (0 until config.targetDataIterate).toList().map {
            ResultConverter.fromJson(JsonImporter("$dir/$it/PaddingMethod/result").getString())
        }
        val rs2 = (0 until config.targetDataIterate).toList().map {
            ResultConverter.fromJson(JsonImporter("$dir/$it/利益最大化-平均/result").getString())
        }
        println(rs1.map {
            println(it.providerResults[0].profit)
            it.providerResults[0].profit
        }.average())
        println("Padding Method")
        println(rs1.map { it.providerResults[0].profit }.average())
        println(rs1.map { it.providerResults[0].profit }.sd())

        println("取引価格平均")
        println(rs2.map { it.providerResults[1].profit }.average())
        println(rs2.map { it.providerResults[1].profit }.sd())
    }
}

