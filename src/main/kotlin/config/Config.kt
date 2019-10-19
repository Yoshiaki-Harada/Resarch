package config

import converter.ConfigConverter
import impoter.JsonImporter


/**
 * 設定用ファイル
 *
 * @property provider
 * @property providerTimeMin
 * @property providerTimeMax
 * @property providerValueMin
 * @property providerValueMax
 * @property providerResourceNumber
 * @property requester
 * @property requesterTimeMin
 * @property requesterTimeMax
 * @property requesterValueMin
 * @property requesterValueMax
 * @property bidNumber
 * @property resource
 * @property penalty
 * @property bidderFile
 * @property bidDir
 * @property lpFile
 * @property lpDir
 * @property auction
 * @property resultFile
 * @property resultDir
 * @property profitRate
 * @property period 1期間あたりのTs
 * @property targetAuction 計算させたいオークションのリスト
 * @property targetData　計算させたいデータセットのリスト
 * @property targetDataIterate データセットの繰り返し数
 * @property items  Excelに出力する評価指標のリスト
 */
data class Config(var provider: Int, /*提供企業数*/
                  var providerTimeMin: Double, /*提供企業の最小提供時間*/
                  var providerTimeMax: Double, /*提供企業の最大提供時間*/
                  var providerValueMin: Double, /*提供企業の最小コスト*/
                  var providerValueMax: Double, /*提供企業の最大コスト*/
                  var providerResourceNumber: Int, /*提供リソース数*/
                  var requester: Int, /*要求企業数*/
                  var requesterTimeMin: Double, /*要求企業の最小要求時間*/
                  var requesterTimeMax: Double, /*要求企業の最大要求時間*/
                  var requesterValueMin: Double, /*要求企業の最小予算*/
                  var requesterValueMax: Double, /*要求企業の最大予算*/
                  var bidNumber: Int, /*入札数*/
                  var resource: Int, /*リソースの種類*/
                  var penalty: Double, /*ペナルティ係数*/
                  var bidderFile: String, /*入札者用Jsonファイルのファイル名(Bidディレクトリ直下)*/
                  var bidDir: String, /*入札者用Jsonファイルのディレクトリ名*/
                  var lpFile: String, /*LPファイルのファイル名(LPディレクトリ直下)*/
                  val lpDir: String,/*LPファイルのディレクトリ名*/
                  var auction: Int, /*オークションの種類*/
                  var resultFile: String, /*結果出力用のファイル名(resultDir直下)*/
                  var resultDir: String,/*結果出力用のディレクトリ名*/
                  var profitRate: Int /*その他*/,
                  val period: Double/*1期間何Tsであるか*/,
                  val targetAuction: List<String>,
                  var targetData: List<String>,
                  val targetDataIterate: Int?,
                  val items: List<String>,
                  val lieProviderNumber: Int) {
    companion object {
        fun fromJson(filePath: String): Config {
            return ConfigConverter.fromJson(JsonImporter(filePath).getString())
        }
    }
}