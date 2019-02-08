package result

class BidCal() {
    var payment = 0.0
    var profit = 0.0

    fun addPayment(double: Double) {
        this.payment += double
    }

    fun addProfit(double: Double) {
        this.profit += double
    }

}