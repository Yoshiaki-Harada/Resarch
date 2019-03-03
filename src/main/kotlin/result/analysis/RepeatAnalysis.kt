package result.analysis


fun main(args: Array<String>) {
    // TODO 外部ファイルに追い出したい
    val auctions = listOf(
            "利益最大化-取引価格-平均",
            "コスト最小化-ペナルティ-10000.0-平均",
            "提供単価最小化-ペナルティ-10000.0-利益率30%",
            "提供単価最小化-ペナルティ-10000.0-利益率40%",
            "提供単価最小化-ペナルティ-10000.0-利益率50%",
            "提供単価最小化-ペナルティ-10000.0-利益率60%")

    var min = 50.0
    var max = 150.0
    for (i in 0 until 8) {
        auctions.forEach { run(min, max, it) }
        min += 50.0
        max += 50.0
    }
}