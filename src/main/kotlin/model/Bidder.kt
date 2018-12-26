import adapter.BidAdapter
import adapter.BundleAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class Bidder() {
    val bids = ArrayList<Bid>()

    constructor(bid: Bid) : this() {
        bids.add(bid)
    }

    fun add(bid: Bid) {
        bids.add(bid)
    }

    fun makeData(number: Int, item: Int, valueMax: Int) {
        //どのような組合せを要求するか
        val rand = Random()
        //要求する財の種類の数を決定
        val itemNumber = rand.nextInt(item)
        val shuffuled = (0..item).toList().shuffled(java.util.Random(0))
        val bundleItem = shuffuled.take(itemNumber).sorted()
        val bundle = ArrayList<Double>()
        for (i in 0 until item) {
            //iがbundleItemSortのindexと一致すれば
            if (bundleItem.contains(i)) {
                //時間を決定
                val time = rand.nextInt(10).toDouble()
                bundle.add(time)
                continue
            }
            bundle.add(0.0)
        }
        //各入札の評価値を決める
        val value = rand.nextInt(valueMax).toDouble()
        add(Bid(value, bundle))
    }

    fun toJson(): String {
        val moshi = Moshi.Builder()
                .add(BidAdapter())
                .add(BundleAdapter())
                .build()

        return moshi.adapter(Bidder::class.java).indent("   ").toJson(this)
    }


    fun fromJson(json: String): Bidder {
        val LENIENT_FACTORY = object : JsonAdapter.Factory {
            override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*> {
                return moshi.nextAdapter<Any>(this, type, annotations).lenient()
            }
        }
        val moshi = Moshi.Builder()
                .add(LENIENT_FACTORY)
                .add(BidAdapter())
                .add(BundleAdapter())
                .build()

        return moshi.adapter(Bidder::class.java).fromJson(json)!!
    }
}
