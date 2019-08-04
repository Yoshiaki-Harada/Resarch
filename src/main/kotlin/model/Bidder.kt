package model


class Bidder() {
    val bids: MutableList<Bid> = mutableListOf()
    var id: Int? = null

    constructor(bid: Bid) : this() {
        bids.add(bid)
    }

    fun add(bid: Bid): Bidder {
        bids.add(bid)
        return this
    }

    fun add(bids: List<Bid>): Bidder {
        bids.forEach {
            this.add(it)
        }
        return this
    }

}
