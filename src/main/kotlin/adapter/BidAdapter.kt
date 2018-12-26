package adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import model.Bid

class BidAdapter {
    @ToJson
    fun arrayListToJson(list: ArrayList<Bid>) : List<Bid> = list

    @FromJson
    fun arrayListFromJson(list: List<Bid>) : ArrayList<Bid> = ArrayList(list)
}