package dataset

import config.Config
import converter.BidderConverter
import impoter.JsonImporter
import model.Bid
import model.Bidder
import model.Value
import writer.JsonWriter
import java.io.File
import java.util.*

fun main() {
    val config = Config.fromJson("config-dataset")
    val DATASET_ITERATE = 10
    val bidDir = config.bidDir

    for (i in 0 until DATASET_ITERATE) {
        val bidders = mutableListOf<Bidder>()
        for (index in 0 until config.provider + config.requester) {
            bidders.add(BidderConverter.fromJson(JsonImporter("$bidDir/$i/bidder$index").getString()))
        }

        val lieProviders = bidders
                .subList(0, config.provider)
                .mapIndexed { bidIndex, bidder ->
                    Bidder().add(
                            bidder.bids.map { it ->
                                Bid(Value(it.value.tValue, createRandomValue(0.0, 0.3).times(it.value.tValue)), it.bundle)
                            })
                }

        val lieRequesters = bidders
                .subList(config.provider, config.provider + config.requester)
                .map { bidder ->
                    Bidder().add(
                            bidder.bids.map {
                                Bid(Value(it.value.tValue, createRandomValue(0.0, 0.3).times(it.value.tValue).times(-1.0)), it.bundle)
                            })
                }
        val afterBidders = lieProviders.plus(lieRequesters)
        val dirName = "Bid/real-case/req6-pro4/lie-data/$i"
        val dir = File(dirName).absoluteFile
        dir.mkdirs()
        afterBidders.forEachIndexed { index, bidder ->
            JsonWriter("$dirName/bidder$index").makeFile(BidderConverter.toJson(bidder))
        }
    }
}

fun createRandomValue(min: Double, max: Double) = Random().doubles(1, min, max).toArray().toList()[0]