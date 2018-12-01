package adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.lang.reflect.Array

class BundleAdapter {
    @ToJson
    fun arrayListToJson(list: ArrayList<Double>): List<Double> = list

    @FromJson fun arrayListFromJson(list: List<Double>) : ArrayList<Double> = ArrayList(list)

}