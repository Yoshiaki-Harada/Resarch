package result.analysis

import config.Config
import converter.ResultConverter
import impoter.JsonImporter

/**
 * 繰り返した結果の平均と標準偏差を求めるためのmain関数
 */
fun main(args: Array<String>) {
    val config = Config.fromJson("config")
    config.targetData.forEach { dataset ->
        config.targetAuction.forEach {
            if (config.targetDataIterate == null) {
                throw Exception("ite がnullです")
            }
            runCal(it, config.resultDir, dataset, config.targetDataIterate!!)
        }
    }
}


/**
 *  $resultDir/$dataset/$ite/$auction のresult.jsonの結果をまとめて，$resultDir/$datasetにauction名で保存
 *
 * @param auction
 * @param resultDir
 * @param dataSet
 * @param ite
 */
fun runCal(auction: String, resultDir: String, dataSet: String, ite: Int) {
    val dir = "$resultDir/$dataSet"

    val rs = (0 until ite).toList().map {
        ResultConverter.fromJson(JsonImporter("$dir/$it/$auction/result").getString())
    }
    println(dir)
    println(auction)
    println("provider ${rs.map { it.providerResults[0].profit }}")
}

