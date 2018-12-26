import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class Resource() {

    var time: Array<Double>? = null

    constructor(timeArray: Array<Double>) : this() {
        time = timeArray
    }

    constructor(json: String) : this() {
        val LENIENT_FACTORY = object : JsonAdapter.Factory {
            override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*> {
                return moshi.nextAdapter<Any>(this, type, annotations).lenient()
            }
        }
        val moshi = Moshi.Builder()
                .add(LENIENT_FACTORY)
                .build()

        time = moshi.adapter(Resource::class.java).fromJson(json)!!.time
    }


    fun toJson(): String {
        val moshi = Moshi.Builder().build()

        return moshi.adapter(Resource::class.java).indent("   ").toJson(this)

    }
}
