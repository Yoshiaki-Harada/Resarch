import adapter.BidAdapter
import adapter.BundleAdapter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import okio.BufferedSource
import java.io.BufferedReader
import java.io.File
import java.lang.reflect.Type


class Bidder(){
    val bids = ArrayList<Bid>()

    fun add(bid :Bid){
        bids.add(bid)
    }

    fun toJson() :String{
        val moshi = Moshi.Builder()
                .add(BidAdapter())
                .add(BundleAdapter())
                .build()
        return moshi.adapter(Bidder::class.java).toJson(this)
    }

    fun fromJson(json :String):Bidder{
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
