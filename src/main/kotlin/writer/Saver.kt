package writer

import config.Config
import converter.BidderConverter
import converter.ConfigConverter
import converter.ResultConverter
import cplex.lpformat.Object
import model.Bidder
import result.Result
import java.io.File


/*
0 VCG用
1 コスト最小化
2 利益最大化 取引（平均）
3 コスト最小化（ペナルティ）取引（平均）
4　コスト最小化（ペナルティ）取引（提供単価）
 */

object ResultWriter {
    fun save(bidders: List<Bidder>, obj: Object, result: Result, config: Config) {
        val dirName: String = "Result" + when (config.auction) {
            0 -> "vcg"
            1 -> "コスト最小化-取引価格-平均"
            2 -> "利益最大化-取引価格-平均"
            3 -> "コスト最小化-ペナルティ-${config.penalty}-平均"
            4 -> "提供単価最小化-ペナルティ-${config.penalty}-利益率${config.profitRate}%"
            else -> {
                "error"
            }
        }
        val dir = File("${dirName}").absoluteFile
        dir.mkdir()
        //resultFile
        writer.JsonWriter("${dirName}/result").makeFile(ResultConverter.toJson(result))
        //configFile
        writer.JsonWriter("${dirName}/config").makeFile(ConfigConverter.toJson(config))
        //lpFile
        val lpFile = File("${dirName}lp.file").absoluteFile
        lpFile.copyTo(File(config.lpFile).absoluteFile)
        //bidFileを詰め込みたい
        bidders.forEachIndexed { index, bidder ->
            JsonWriter(dirName + "${dirName}bidder$index").makeFile(BidderConverter.toJson(bidder))
        }
    }
}