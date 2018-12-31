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
}