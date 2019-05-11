package result

/**
 * 企業用の結果クラス
 *
 * @property id
 * @property payment
 * @property profit
 */
open class BidderResult(val id: Int,
                        val payment: Double,
                        val profit: Double)

/**
 * 提供企業用の結果クラス
 *
 * @property timeRatio
 * @property beforeAvailabilityRatio 取引を行う前の稼働率
 * @property afterProviderAvailabilityRatio 取引後の稼働率
 * @constructor
 *
 *
 * @param id
 * @param payment
 * @param profit
 */
class ProviderResult(id: Int,
                     payment: Double,
                     profit: Double,
                     val timeRatio: Double,
                     val beforeAvailabilityRatio: Double,
                     val afterProviderAvailabilityRatio: Double) : BidderResult(id, payment, profit)

