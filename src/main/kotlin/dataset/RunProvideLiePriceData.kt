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
 * 虚偽申告用のデータを作成する
 * @param args
 */
fun main(args: Array<String>) {

    val SUPPLY_DATASET_NUMBER = 1
    val DATASET_ITERATE = 10
    val LieProviderNumber = 1 //人数
    val lieMap =
            mapOf(
                    "10%" to 0.1,
                    "20%" to 0.2,
                    "30%" to 0.3,
                    "40%" to 0.4,
                    "50%" to 0.5,
                    "60%" to 0.6,
                    "70%" to 0.7,
                    "80%" to 0.8,
                    "90%" to 0.9,
                    "100%" to 1.0
            )

    val config = Config.fromJson("config-dataset")

    // 提供割合の違いの繰り返し
    for (s in 0 until SUPPLY_DATASET_NUMBER) {
        var minSupply = config.providerTimeMin + s * 50.0
        var maxSupply = config.providerTimeMax + s * 50.0

        val bidDir = "Bid/Provider=25/cost-range/lie-ratio/2.5/0%"

        for (p in 0 until 3) {
            var lie = 10 + 10 * p

            // データセット数による繰り返し
            for (i in 0 until DATASET_ITERATE) {
                val bidders = mutableListOf<Bidder>()
                for (index in 0 until config.provider + config.requester) {
                    bidders.add(BidderConverter.fromJson(JsonImporter("$bidDir/$i/bidder$index").getString()))
                    println("$bidDir/$i/bidder$index")
                }
                val copyBidders = bidders
                        .subList(0, config.provider)
                        .mapIndexed { bidIndex, bidder ->
                            Bidder().add(
                                    bidder.bids.mapIndexed { index, it ->
                                        if (bidIndex < LieProviderNumber) {
                                            Bid(Value(it.value.tValue, lieMap["$lie%"]!!.times(it.value.tValue)), it.bundle)
                                        } else {
                                            it
                                        }
                                    }
                            )
                        }

                val afterBidders = copyBidders.plus(bidders.subList(config.provider, config.provider + config.requester))
                val dirName = "Bid/Provider=25/cost-range/lie-ratio/2.5/$lie%/$i"
                val dir = File(dirName).absoluteFile
                dir.mkdirs()
                afterBidders.forEachIndexed { index, bidder ->
                    JsonWriter("$dirName/bidder$index").makeFile(BidderConverter.toJson(bidder))
                }
            }
        }
//        var lie = 20
//
//        // データセット数による繰り返し
//        for (i in 0 until DATASET_ITERATE) {
//            val bidders = mutableListOf<Bidder>()
//            for (index in 0 until config.provider + config.requester) {
//                bidders.add(BidderConverter.fromJson(JsonImporter("$bidDir/$i/bidder$index").getString()))
//                println("$bidDir/$i/bidder$index")
//            }
//            val copyBidders = bidders
//                    .subList(0, config.provider)
//                    .mapIndexed { bidIndex, bidder ->
//                        Bidder().add(
//                                bidder.bids.mapIndexed { index, it ->
//                                    if (bidIndex < LieProviderNumber) {
//                                        Bid(Value(it.value.tValue, lieMap["$lie%"]!!.times(it.value.tValue)), it.bundle)
//                                    } else {
//                                        it
//                                    }
//                                }
//                        )
//                    }
//
//            val afterBidders = copyBidders.plus(bidders.subList(config.provider, config.provider + config.requester))
//            val dirName = "Bid/Provider=25/cost/lie-ratio/$LieProviderNumber/$i"
//            val dir = File(dirName).absoluteFile
//            dir.mkdirs()
//            afterBidders.forEachIndexed { index, bidder ->
//                JsonWriter("$dirName/bidder$index").makeFile(BidderConverter.toJson(bidder))
//            }
//        }
    }
}
