package result

data class Result(val objectValue: Double,
                  val sumCost: Double,
                  val sumProfit: Double,
                  val x: DoubleArray,
                  val winBidNUmber: Int,
                  val providerResults: List<BidderResult>,
                  val requesterResults: List<BidderResult>,
                  val providerProfitAve: Double,
                  val providerProfitSD: Double,
                  val requesterProfitAve: Double,
                  val requesterProfitSD: Double,
                  val tradeAve: Double,
                  val tradeSD: Double,
                  val providerBidResults: List<BidResult>,
                  val requesteBidResults: List<BidResult>)