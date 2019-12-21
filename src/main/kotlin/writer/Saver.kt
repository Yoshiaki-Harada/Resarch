package writer

import config.Config
import converter.BidderConverter
import converter.ConfigConverter
import converter.ResultConverter
import model.Bidder
import result.Result
import java.io.File

/**
 * 実験に使用したものを保存する為のオブジェクト
 */
object Saver {
    /**
     * $resultDir/$resultFile に結果を出力する
     */
    fun run(bidders: List<Bidder>, result: Result, config: Config, targetAuction: String, targetData: String, targretIte: Int) {
        val dirName = "${config.resultDir}${config.bidDir.replace("Bid", "")}/$targetData/$targretIte/$targetAuction"
        println("Directory: $dirName")
        val dir = File("${dirName}").absoluteFile
        dir.mkdirs()
        // resultFile
        writer.JsonWriter("${dirName}/result").makeFile(ResultConverter.toJson(result))
        // configFile
        writer.JsonWriter("${dirName}/default").makeFile(ConfigConverter.toJson(config))
        // lpFile
        val lpFile = File("${config.lpDir}/${config.lpFile}.lp").absoluteFile
        if (File("${dirName}/lp.lp").absoluteFile.delete())
            println("${dirName}/lp.lp を削除しました")
        lpFile.copyTo(File("${dirName}/lp.lp").absoluteFile)

        // bidFileを詰め込みたい
        val bidDir = File("${dirName}/Bid").absoluteFile
        bidDir.mkdir()
        bidders.forEachIndexed { index, bidder ->
            JsonWriter("${dirName}/Bid/bidder$index").makeFile(BidderConverter.toJson(bidder))
        }
    }
}