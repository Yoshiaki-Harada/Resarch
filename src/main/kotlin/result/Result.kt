package result

data class Result(val objectValue: Double,
                  val sumCost: Double,
                  val sumProfit: Double,
                  val x: DoubleArray,
                  val providerResults: List<BidderResult>,
                  val requesterResults: List<BidderResult>,
                  val providerBidResults: List<BidResult>,
                  val requesteBidResults: List<BidResult>)