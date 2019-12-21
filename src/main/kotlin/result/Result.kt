package result

/**
 * 結果クラス
 *
 * @property objectValue 目的関数値
 * @property sumCost    総コスト
 * @property sumProfit  総利益
 * @property x  決定変数
 * @property winBidRation   勝者となった入札数(要求)
 * @property providerResults    提供側の結果のリスト
 * @property requesterResults   要求側の結果のリスト
 * @property providerProfitAve  提供側の結果の平均値
 * @property providerProfitSD   提供側の結果の分散
 * @property providerTimeRatioAve  実際に提供した時間/提供可能時間の平均
 * @property providerTimeRatioSD   実際に提供した時間/提供可能時間の分散
 * @property requesterProfitAve 要求側利益の平均
 * @property requesterProfitSD  要求側利益の分散
 * @property requesterPayAve   取引価格の平均
 * @property requesterPaySD    取引価格の分散
 * @property providerBidResults 提供側の入札の結果のリスト
 * @property requesterBidResults　要求側の入札の結果のリスト
 * @property beforeProviderAvailabilityRatioAve 提供側の取引前の稼働率の平均
 * @property afterProviderAvailabilityRatioAve  提供側の取引後の稼働率の平均
 * @property providerRevenueDensityAve 提供したリソースの1Ts辺りの報酬額の平均
 * @property providerRevenueDensitySD 提供したリソースの1Ts辺りの報酬額の分散
 * @property requesterLiarsResult 嘘をつく要求企業群の結果
 * @property providerLiarsResult 嘘をつく提供企業群の結果
 */
data class Result(val objectValue: Double,
                  val sumProfit: Double,
                  val x: List<Double>,
                  val winBidRatio: Double,
                  val providerResults: List<BidderResult>,
                  val requesterResults: List<BidderResult>,
                  val providerProfitAve: Double,
                  val providerProfitSD: Double,
                  val providerTimeRatioAve: Double,
                  val providerTimeRatioSD: Double,
                  val requesterProfitAve: Double,
                  val requesterProfitSD: Double,
                  val requesterPayAve: Double,
                  val requesterPaySD: Double,
                  val providerRevenueAve: Double,
                  val providerRevenueSD: Double,
                  val providerBidResults: List<BidResult>,
                  val requesterBidResults: List<BidResult>,
                  val beforeProviderAvailabilityRatioAve: Double,
                  val afterProviderAvailabilityRatioAve: Double,
                  val providerRevenueDensityAve: Double,
                  val providerRevenueDensitySD: Double,
                  val sumPay: Double,
                  val sumRevenue: Double,
                  val providerLiarsResult: ProviderLiarsResult?,
                  val auctioneerProfit: Double,
                  val requesterLiarsResult: RequesterLiarsResult?,
                  val calculationTimeMillis: Long,
                  val requesterLiarResult: RequesterLiarResult,
                  val providerLiarResult: ProviderLiarResult)

/**
 * 虚偽申告をした入札者の結果用のクラス
 *
 * @property providerProfitAve
 * @property providerProfitSD
 */
data class ProviderLiarsResult(val providerProfitAve: Double = 0.0,
                               val providerProfitSD: Double = 0.0,
                               val providerRevenueDensityAve: Double = 0.0,
                               val providerRevenueDensitySD: Double = 0.0)

data class RequesterLiarsResult(val requesterProfitAve: Double = 0.0,
                                val requesterProfitSD: Double = 0.0,
                                val requesterPayAve: Double = 0.0,
                                val requesterPaySD: Double = 0.0)

data class ProviderLiarResult(val profit: Double = 0.0, val reward: Double=0.0)

data class RequesterLiarResult(val profit: Double = 0.0, val pay: Double=0.0)