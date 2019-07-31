package dataset

import config.Config
import model.Bidder

interface BidderDataMaker : DataMaker<List<Bidder>> {
    override fun run(config: Config): List<Bidder>
}

object RequesterDataMakerImpl : BidderDataMaker {
    override fun run(config: Config): List<Bidder> {
        return List(config.requester) { Bidder().add(List(config.bidNumber) { RequesterBidDataMakerImpl.run(config) }) }
    }
}

/**
 * providerResourceNumberで指定された数のリソースを提供する
 */
object ProviderDataMakerImpl : BidderDataMaker {
    override fun run(config: Config): List<Bidder> {
        return List(config.provider) {
            val rands = (0 until config.resource).toList().shuffled()
            //提供するresourceのリストを作る
            val resource = rands.subList(0, config.providerResourceNumber).sorted()

            Bidder().add(List(config.resource) { index ->
                //提供するリソースであれば，その種類を引数に渡す，提供しなければ-1を渡すことで0の入札を作成する
                if (resource.contains(index))
                    ProviderBidDataMakerProvideTimeImpl.run(config, index)
                else
                    ProviderBidDataMakerProvideTimeImpl.run(config, -1)
            })
        }
    }
}