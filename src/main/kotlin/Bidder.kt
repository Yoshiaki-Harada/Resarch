import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


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
}
