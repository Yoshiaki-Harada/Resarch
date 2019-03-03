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
                      val providerTimeRatioSD: Double)