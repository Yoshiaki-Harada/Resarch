package converter

import com.google.gson.annotations.JsonAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import config.Config
import model.Bidder
import model.Resource
import result.BidCal
import result.BidderCal
import result.Result
import result.analysis.Conclusion
import java.lang.reflect.Type

/**
 * Jsonへの変換用オブジェクト
 */
interface JsonConverter<T> {
    val moshi: Moshi.Builder
        get() = Moshi.Builder()
    val LENIENT_FACTORY: JsonAdapter.Factory
        get() = object : JsonAdapter.Factory {
            override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*> {
                return moshi.nextAdapter<Any>(this, type, annotations).lenient()
            }
        }

    fun toJson(t: T): String
    fun fromJson(s: String): T

}

object ResultConverter : JsonConverter<Result> {

    override fun toJson(t: Result): String {
        return moshi.build().adapter(Result::class.java).indent("   ").toJson(t)
    }

    override fun fromJson(s: String): Result {
        return moshi.add(LENIENT_FACTORY).build().adapter(Result::class.java).fromJson(s)!!
    }
}

object BidderConverter : JsonConverter<Bidder> {

    override fun toJson(t: Bidder): String {
        return moshi.build().adapter(Bidder::class.java).indent("   ").toJson(t)
    }

    override fun fromJson(s: String): Bidder {
        return moshi.add(LENIENT_FACTORY).build().adapter(Bidder::class.java).fromJson(s)!!
    }
}

object ResourceConverter : JsonConverter<Resource> {
    override fun toJson(t: Resource): String {
        return moshi.build().adapter(Resource::class.java).indent("   ").toJson(t)
    }

    override fun fromJson(s: String): Resource {
        return moshi.add(LENIENT_FACTORY).build().adapter(Resource::class.java).fromJson(s)!!
    }
}

object ConfigConverter : JsonConverter<Config> {

    override fun toJson(t: Config): String {
        return moshi.build().adapter(Config::class.java).indent("   ").toJson(t)
    }

    override fun fromJson(s: String): Config {
        return moshi.add(LENIENT_FACTORY).build().adapter(Config::class.java).fromJson(s)!!
    }
}

object BidderCalConverter : JsonConverter<BidderCal> {

    override fun toJson(t: BidderCal): String {
        return moshi.build().adapter(BidderCal::class.java).indent("   ").toJson(t)
    }

    override fun fromJson(s: String): BidderCal {
        return moshi.add(LENIENT_FACTORY).build().adapter(BidderCal::class.java).fromJson(s)!!
    }
}

object ConclusionConverter : JsonConverter<Conclusion> {

    override fun toJson(t: Conclusion): String {
        return BidderCalConverter.moshi.build().adapter(Conclusion::class.java).indent("   ").toJson(t)
    }

    override fun fromJson(s: String): Conclusion {
        return BidderCalConverter.moshi.add(BidderCalConverter.LENIENT_FACTORY).build().adapter(Conclusion::class.java).fromJson(s)!!
    }
}

