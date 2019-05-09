package dataset

import config.Config
import model.Bid
import model.Value
import java.util.*

interface BidDataMaker : DataMaker<Bid> {
    override fun run(config: Config): Bid
}

object RequesterBidDataMakerImpl : BidDataMaker {
    override fun run(config: Config): Bid {
        val bundle = Random()
                .doubles(config.resource.toLong(),
                        config.requesterTimeMin,
                        config.requesterTimeMax)
                .toArray()
                .toList()
        //予算の計算
        val weight = Random().doubles(1, config.requesterValueMin, config.requesterValueMax).toArray().toList()[0]
        val value = Value(bundle.sum() * weight, 0.0)
        return Bid(value, bundle)
    }
}

/*
提供用の入札を作成する
提供時間はproviderMin~providerMaxの乱数
コストはproviderValueMin~providerValueMaxの乱数
 */
object ProviderBidDataMakerImpl {
    fun run(config: Config, resource: Int): Bid {
        val bundle = List(config.resource) {
            Random().doubles(1, config.providerTimeMin, config.providerTimeMax)
                    .toArray()
                    .toList()[0]
        }.mapIndexed { r, d ->
            //提供するリソースの種類であれば
            if (r == resource) {
                d
            } else {
                0.0
            }
        }
        //予算
        val value = when (bundle.all { it == 0.0 }) {
            true -> Value(0.0, 0.0)
            else -> Value(Random().doubles(1, config.providerValueMin, config.providerValueMax).toArray().toList()[0], 0.0)
        }
        return Bid(value, bundle)
    }
}