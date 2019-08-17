package result

/**
 * 結果クラス
 *
 * @property objectValue 目的関数値
 * @property sumCost    総コスト
 * @property sumProfit  総利益
 * @property x  決定変数
 * @property winBidNUmber   勝者となった入札数(要求)
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
 * @property providerRewardDensityAve 提供したリソースの1Ts辺りの報酬額の平均
 * @property providerRewardDensitySD 提供したリソースの1Ts辺りの報酬額の分散
 */
data class Result(val objectValue: Double,
                  val sumCost: Double,
                  val sumProfit: Double,
                  val x: DoubleArray,
                  val winBidNUmber: Int,
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
                  val providerRewardAve: Double,
                  val providerRewardSD: Double,
                  val providerBidResults: List<BidResult>,
                  val requesterBidResults: List<BidResult>,
                  val beforeProviderAvailabilityRatioAve: Double,
                  val afterProviderAvailabilityRatioAve: Double,
                  val providerRewardDensityAve: Double,
                  val providerRewardDensitySD: Double)