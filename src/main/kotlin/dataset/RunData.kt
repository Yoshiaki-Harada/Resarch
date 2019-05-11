package dataset

import config.Config
import converter.BidderConverter
import writer.JsonWriter
import java.io.File

fun main(args: Array<String>) {
    val config = Config.fromJson("config")
    val SUPPLY_DATA_NUMBER = 8
    val DATASET_ITERATE = 10

    for (s in 0 until SUPPLY_DATA_NUMBER) {
        val bidDir = "${config.bidDir}/supply-${config.providerTimeMin}-${config.providerTimeMax}"
        for (i in 0 until DATASET_ITERATE) {
            val dir = File("$bidDir/$i").absoluteFile
            dir.mkdirs()
            // データ生成
            val bidders = ProviderDataMakerImpl.run(config).plus(RequesterDataMakerImpl.run(config))
            bidders.forEachIndexed { index, bidder ->
                JsonWriter("$bidDir/$i/bidder$index").makeFile(BidderConverter.toJson(bidder))
            }
        }
        config.providerTimeMin += 50
        config.providerTimeMax += 50
    }

}