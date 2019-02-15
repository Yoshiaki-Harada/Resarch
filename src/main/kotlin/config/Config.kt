package config

import Impoter.JsonImporter
import converter.ConfigConverter

//TODO nullを許容する設定
class Config(val provider: Int, /*提供企業数*/
             val providerTimeMin: Double, /*提供企業の最小提供時間*/
             val providerTimeMax: Double, /*提供企業の最大提供時間*/
             val providerValueMin: Double, /*提供企業の最小コスト*/
             val providerValueMax: Double, /*提供企業の最大コスト*/
             val providerResourceNumber: Int, /*提供リソース数*/
             val requester: Int, /*要求企業数*/
             val requesterTimeMin: Double, /*要求企業の最小要求時間*/
             val requesterTimeMax: Double, /*要求企業の最大要求時間*/
             val requesterValueMin: Double, /*要求企業の最小予算*/
             val requesterValueMax: Double, /*要求企業の最大予算*/
             val bidNumber: Int, /*入札数*/
             val resource: Int, /*リソースの種類*/
             val penalty: Double, /*ペナルティ係数*/
             val bidderFile: String, /*入札者用Jsonファイルのファイル名*/
             val lpFile: String, /*LPファイルのファイル名*/
             val auction: Int, /*オークションの種類*/
             val resultFile: String, /*結果出力用のファイル名*/
             val tmpFile: String /*その他*/) {
    companion object {
        fun fromJson(filePath: String): Config {
            return ConfigConverter.fromJson(JsonImporter(filePath).getString())
        }
    }
}