package result

open class BidderResult(val id: Int,
                        val payment: Double,
                        val profit: Double)

class ProviderResult(id: Int,
                     payment: Double,
                     profit: Double,
                     val timeRatio: Double,
                     val beforeAvailabilityRatio: Double,
                     val afterProviderAvailabilityRatio: Double) : BidderResult(id, payment, profit)

