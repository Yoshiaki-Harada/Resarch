import config.Config
import java.math.RoundingMode

object Util {

    fun convertDimension(doubleArray: DoubleArray, indexDef: List<Int>): List<DoubleArray> {
        val mutableList = mutableListOf<DoubleArray>()
        var c = 0
        indexDef.forEach { it ->
            mutableList.add(doubleArray.copyOfRange(c, c + it))
            c += it
        }
        val list: List<DoubleArray> = mutableList
        return list
    }

    fun convertDimension(doubleList: List<Double>, indexDef: List<Int>): List<List<Double>> {
        val mutableList = mutableListOf<List<Double>>()
        var c = 0
        indexDef.forEach { it ->
            mutableList.add(doubleList.subList(c, c + it))
            c += it
        }
        val list: List<List<Double>> = mutableList
        return list
    }

    fun convertDimension4(doubleArray: DoubleArray, requesterBidSize: List<Int>, providerBidSize: List<Int>, config: Config): List<List<List<DoubleArray>>> {
        val mutableList0 = mutableListOf<DoubleArray>()
        var c = 0
        println("requester $providerBidSize")
        println("requester $requesterBidSize")
        for (i in 0 until config.provider) {
            for (r in 0 until config.resource)
                requesterBidSize.forEach {
                    mutableList0.add(doubleArray.copyOfRange(c, c + it))
                    c += it
                }
        }

        val mutableList1 = mutableListOf<List<DoubleArray>>()
        val sum = requesterBidSize.sum()
        c = 0
        for (x in 0 until config.provider) {
            for (i in 0 until config.resource) {
                mutableList1.add(mutableList0.subList(c, c + config.requester))
                c += config.requester
            }
        }

        c = 0
        val mutableList2 = mutableListOf<MutableList<List<DoubleArray>>>()
        providerBidSize.forEach {
            mutableList2.add(mutableList1.subList(c, c + it))
            c += it
        }

        return mutableList2
    }
}

// 標準偏差を求める
fun List<Double>.sd(): Double {
    return Math.sqrt(this.map { Math.pow(it - this.average(), 2.0) }.sum() / this.size)
}

/**
 * 1次元の配列を4次元のリストに変換
 * 動的に入札数を変更させている時には使えない
 *
 * @param x
 * @param config
 * @return
 */
fun convert(x: List<Double>, config: Config): List<List<List<List<Double>>>> {
    val xList = x.map { it.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble() }.toList()
    val l = xList.windowed(size = config.resource * config.requester * config.bidNumber, step = config.resource * config.requester * config.bidNumber)
    return l.map {
        it.windowed(config.requester * config.bidNumber, config.requester * config.bidNumber).map { l ->
            l.windowed(config.bidNumber, config.bidNumber)
        }
    }
}


fun Double.rounding() = this.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()
