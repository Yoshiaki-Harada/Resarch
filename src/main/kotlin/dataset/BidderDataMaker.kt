package dataset

import config.Config
import model.Bid
import model.Bidder
import java.util.*

interface BidderDataMaker : DataMaker<List<out Bidder>> {
    override fun run(config: Config): List<Bidder>
}

object RequesterDataMakerImpl : BidderDataMaker {
    override fun run(config: Config): List<Bidder> {
        return List(config.requester) { Bidder().add(List(config.bidNumber) { RequesterBidDataMakerImpl.run(config) }) }
    }
}

object ProviderDataMakerImpl : BidderDataMaker {
    override fun run(config: Config): List<Bidder> {
        return List(config.provider) {
            val rands = (0..(config.resource - 1)).toList().shuffled()
            val resource = rands.subList(0, config.providerResourceNumber).sorted()
            Bidder().add(List(config.resource) { index ->
                println("index $index")
                println(resource)
                if (resource.contains(index))
                    ProviderBidDataMakerImpl.run(config, index)
                else
                    ProviderBidDataMakerImpl.run(config, -1)
            })
        }
    }
}