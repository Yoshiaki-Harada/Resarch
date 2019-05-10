package result

/**
 * 結果計算用のクラス
 *
 */
class BidCal {
    var payment = 0.0
    var profit = 0.0
    var time = 0.0

    fun addPayment(p: Double) {
        this.payment += p
    }

    fun addProfit(p: Double) {
        this.profit += p
    }

    fun addTime(t: Double) {
        this.time += t
    }

}