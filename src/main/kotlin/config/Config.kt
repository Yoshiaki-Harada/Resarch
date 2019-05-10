package config

import impoter.JsonImporter
import converter.ConfigConverter

/*
0 VCG用
1 コスト最小化
2 利益最大化 取引（平均）
3 コスト最小化（ペナルティ）取引（平均）
4~　コスト最小化（ペナルティ）取引（提供単価）
 */

class Config(var provider: Int, /*提供企業数*/
             var providerTimeMin: Double, /*提供企業の最小提供時間*/
             val providerTimeMax: Double, /*提供企業の最大提供時間*/
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
             val targetDataIterate: Int?) {
    companion object {
        fun fromJson(filePath: String): Config {
            return ConfigConverter.fromJson(JsonImporter(filePath).getString())
        }
    }

    fun changeAuction(auction: Int, repeat: Int) {
        this.auction = auction
        // 利益率をどの程度変化させるかによって変わる
        this.bidderFile = when (auction) {
            4, 5, 6, 7, 8 -> "${this.bidDir}-profit${this.profitRate}%/$repeat"
            else -> "${this.bidDir}/$repeat"
        }
        this.resultDir = when (auction) {
            0 -> "Result/${this.bidDir.substring(4)}/$repeat/vcg"
            1 -> "Result/${this.bidDir.substring(4)}/$repeat/コスト最小化-取引価格-平均"
            2 -> "Result/${this.bidDir.substring(4)}/$repeat/利益最大化-取引価格-平均"
            3 -> "Result/${this.bidDir.substring(4)}/$repeat/コスト最小化-ペナルティ-${this.penalty}-平均"
            4, 5, 6, 7, 8,9,10,11 -> "Result/${this.bidDir.substring(4)}/$repeat/提供単価最小化-ペナルティ-${this.penalty}-利益率${this.profitRate}%"
            else -> {
                "error"
            }
        }
    }
}