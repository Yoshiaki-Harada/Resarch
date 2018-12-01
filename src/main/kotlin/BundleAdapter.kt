import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.lang.reflect.Array

class BundleAdapter {
    @ToJson
    fun arrayListToJson(list: ArrayList<ArrayList<Double>>): List<List<Double>> = list

    @FromJson
    fun arrayListFromJson(list: List<List<Double>>): ArrayList<ArrayList<Double>> {
        val arrayListDouble = ArrayList<Double>()
        val arrayList = ArrayList<ArrayList<Double>>()
        for (doubleList in list) {
            for (double in doubleList){
                arrayListDouble.add(double)
            }
            arrayList.add(arrayListDouble)
        }
        return arrayList
    }
}