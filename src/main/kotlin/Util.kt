import config.Config

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

    fun convertDimension4(doubleArray: DoubleArray, requesterBidSize: List<Int>, providerBidSize: List<Int>, config: Config): List<List<List<DoubleArray>>> {
        val mutableList0 = mutableListOf<DoubleArray>()
        var c = 0
        println("requester $providerBidSize")
        println("requester $requesterBidSize")
        for (i in 0 until config.provider) {
            for (r in 0 until config.resource)
                requesterBidSize.forEach {
                    println("i: $i, r: $r")
                    println("c: $c, c+it: " + (c + it).toString())
                    mutableList0.add(doubleArray.copyOfRange(c, c + it))
                    c += it
                }
        }

        val mutableList1 = mutableListOf<List<DoubleArray>>()
        val sum = requesterBidSize.sum()
        c = 0
        for (x in 0 until config.provider) {
            for (i in 0 until config.resource) {
                println("listSize: ${mutableList0.size} c,last: $c ${c + config.requester}")
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