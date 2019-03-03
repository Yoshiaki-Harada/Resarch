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

    for (s in 0 until 8) {
        var minSupply = config.providerTimeMin + s * 50.0
        var maxSupply = config.providerTimeMax + s * 50.0

        config.bidDir = "Bid/supply-$minSupply-$maxSupply"
        val a = 4
        config.profitRate = 30
        for (i in 0 until 5){
            config.changeAuction(a, i)
            run(config)
        }

        for (auction in 0 until 7) {
            // 繰り返し　
            for (i in 0 until 5) {
                // auction:オークションの切り替え i:繰り返し
                // auction>=4の場合は利益率のデータを扱う
                if (auction >= 4) {
                    config.profitRate = (auction - 2) * 10 + 20
                    config.changeAuction(auction + 2, i)
                }
                config.changeAuction(auction + 2, i)
                run(config)
            }
        }
    }
}