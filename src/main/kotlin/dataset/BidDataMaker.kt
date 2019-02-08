package dataset

import config.Config
import model.Bid
import model.Value
import java.util.*
import kotlin.streams.toList

interface BidDataMaker : DataMaker<Bid> {
    override fun run(config: Config): Bid
}

object RequesterBidDataMakerImpl : BidDataMaker {
    override fun run(config: Config): Bid {
        val bundle = Random()
                .doubles(config.resource.toLong(),
                        config.requesterTimeMin,
                        config.requesterTimeMax)
                .toList()
        //予算の計算
        val value = Value(bundle.sum() * 1.0, sValue = 0.0)
        return Bid(value, bundle)
    }
}

//Todo 良い方法を考える
object ProviderBidDataMakerImpl {
    fun run(config: Config, resource: Int): Bid {
        println(resource)
        val bundle = List(config.resource) {
            Random().doubles(1, config.providerTimeMin, config.providerTimeMax)
                    .toList()[0]
        }.mapIndexed { r, d ->
            if (r == resource) {
                d
            } else {
                0.0
            }
        }
        val value = when (bundle.all { it -> it == 0.0 }) {
            true -> Value(0.0, 0.0)
            else -> Value(Random().doubles(1, config.providerValueMin, config.providerValueMax).toList()[0], 0.0)
        }
        return Bid(value, bundle)
    }
}