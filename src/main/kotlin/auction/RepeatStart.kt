package auction

import config.Config

/*
0 VCG用
1 コスト最小化
2 利益最大化 取引（平均）
3 コスト最小化（ペナルティ）取引（平均）
4~　コスト最小化（ペナルティ）取引（提供単価）
 */
fun main(args: Array<String>) {
    val config = Config.fromJson("config")

    for (c in 0 until 7) {
        for (i in 0 until 5) {
            // c:オークションの切り替え i:繰り返し
            // c>=4の場合は利益率のデータを扱う
            if (c >= 4) {
                config.profitRate = 30 + c * 10
                config.changeAuction(c + 2, i)
            }
            config.changeAuction(c + 2, i)
            run(config)
        }
    }
}
