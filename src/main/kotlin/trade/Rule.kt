package trade

import model.Bidder

/**
 * 支払い規則と取引の実行を規定する
 *
 */
interface Rule {
    fun run(x: List<List<List<DoubleArray>>>, providers: List<Bidder>, requesters: List<Bidder>): ResultPre
}

/**
 * SingleSidedオークション用
 *
 */
interface SingleSided : Rule {
    fun payment(provider: Bidder, requester: Bidder, bidIndex: Int, resource: Int): Double
}

/**
 * DoubleSided用
 *
 */
interface DobuleSided : Rule {
    fun payment(provider: Bidder, requester: Bidder, bidIndex: Int, resource: Int): Double
}