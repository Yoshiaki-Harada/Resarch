package result.analysis

data class Conclusion(val sumCostAve: Double,
                      val sumCostSD: Double,
                      val sumProfitAve: Double,
                      val sumProfitSD: Double,
                      val providerProfitAve: Double,
                      val providerProfitSD: Double,
                      val requesterProfitAve: Double,
                      val requesterProfitSD: Double,
                      val winBidAve: Double,
                      val winBidSD: Double,
                      val providerTimeRatioAve: Double,
                      val providerTimeRatioSD: Double,
                      val providerBeforeAvailabilityRatioAve: Double,
                      val providerBeforeAvailabilityRatioSD: Double,
                      val providerAfterAvailabilityRatioAve: Double,
                      val providerAfterAvailabilityRatioSD: Double,
                      val requesterPayAve: Double,
                      val requesterPaySD: Double,
                      val providerRevenueAve: Double,
                      val providerRevenueSD: Double,
                      val providerRevenueDensityAve: Double,
                      val providerRevenueDensitySD: Double,
                      val sumPayAve: Double,
                      val sumPaySD: Double,
                      val sumRevenueAve: Double,
                      val sumRevenueSD: Double,
                      val sumProfitIncludeAuctioneerAve: Double,
                      val sumProfitIncludeAuctioneerSD: Double,
                      val liarConclusion: LiarConclusion?)

data class LiarConclusion(
        val providerProfitAve: Double,
        val providerProfitSD: Double,
        val providerRevenueDensityAve: Double,
        val providerRevenueDensitySD: Double
)