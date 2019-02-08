package dataset

import config.Config
import converter.BidderConverter
import writer.JsonWriter


fun main(args: Array<String>) {
    val config = Config.fromJson("config")
    val bidders = ProviderDataMakerImpl.run(config).plus(RequesterDataMakerImpl.run(config))
    bidders.forEachIndexed { index, bidder ->
        JsonWriter(config.bidderFile + "$index").makeFile(BidderConverter.toJson(bidder))
    }
}