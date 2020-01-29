package dataset

import config.Config
import converter.BidderConverter
import impoter.JsonImporter
import model.Bid
import model.Bidder
import model.Value
import writer.JsonWriter
import java.io.File

/**
 * 提供単価を与える為のデータセット
 * @param args
 */
fun main(args: Array<String>) {

    val SUPPLY_DATASET_NUMBER = 1
    val DATASET_ITERATE = 10
    val profitMap =
            mapOf(/*利益率 sValueの割合*/
                    "10%" to 0.11,
                    "20%" to 0.25,
                    "30%" to 0.43,
                    "40%" to 0.66,
                    "50%" to 1.0,
                    "60%" to 1.5,
                    "70%" to 2.3,
                    "80%" to 4.0,
                    "90%" to 9.0
            )

    val config = Config.fromJson("config")

    // 提供割合の違いの繰り返し
    for (s in 0 until SUPPLY_DATASET_NUMBER) {
        var minSupply = config.providerTimeMin + s * 50.0
        var maxSupply = config.providerTimeMax + s * 50.0

        val bidDir = "Bid/supply-${minSupply}-${maxSupply}"

        // 利益率による繰り返し
        for (p in 0 until profitMap.size) {
            var profit = 10 + 10 * p

            // データセット数による繰り返し
            for (i in 0 until DATASET_ITERATE) {
                val bidders = mutableListOf<Bidder>()
                for (index in 0 until config.provider + config.requester) {
                    bidders.add(BidderConverter.fromJson(JsonImporter("$bidDir/$i/bidder$index").getString()))
                    println("$bidDir/$i/bidder$index")
                }
                val copyBidders = bidders
                        .subList(0, config.provider)
                        .map {
                            Bidder().add(
                                    it.bids.map {
                                        Bid(Value(it.value.tValue, profitMap["$profit%"]!!.times(it.value.tValue)), it.bundle)
                                    })
                        }

                val afterBidders = copyBidders.plus(bidders.subList(config.provider, config.provider + config.requester))
                val dir = File("${bidDir}-profit$profit%/$i").absoluteFile
                dir.mkdirs()
                afterBidders.forEachIndexed { index, bidder ->
                    JsonWriter("$bidDir-profit$profit%/$i/bidder$index").makeFile(BidderConverter.toJson(bidder))
                }
            }
        }
    }
}
