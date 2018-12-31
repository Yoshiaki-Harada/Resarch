package lpfile

import config.Config
import lpfile.lpformat.Object
import model.Bidder
import model.Option
import writer.LpWriter
import auction.DoubleAuction
import lpfile.lpformat.Constrait

object ProfitMaxDoubleAuction : LpMaker, DoubleAuction {

    override fun makeLpFile(config: Config, obj: Object, bidders: List<Bidder>, vararg option: Option) {
        val lp = LpWriter(config.lpFile)
        val providers = bidders.subList(0, config.provider)
        val requesters = bidders.subList(config.provider, config.requester)
        //目的関数
        writeObjFunction(lp, obj, providers, requesters)
        //制約条件
        //要求<=提供
        writeSubToTime(lp, obj, bidders, requesters, config)
        //予算制約
    }

    fun writeObjFunction(lp: LpWriter, obj: lpfile.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>) {
        lp.obj(obj)
        lp.newline()
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jに提供するとき1となる変数
                        //provider_iがresource_rをrequester_jの入札nに提供する時間x(正の整数)
                        lp.term(resource.getValue() * bid.bundle[r], "x", "$i$r$j$n")
                    }
                }
            }
        }
        lp.newline()
    }

    fun writeSubToTime(lp: LpWriter, obj: lpfile.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>, config: Config) {
        //全てのresouceについて
        for (r in 0 until config.resource) {

            lp.constrateName("time" + r.toString())
            providers.forEachIndexed { i, provider ->
                provider.bids.forEachIndexed { j, bid ->
                    lp.term(bid.bundle[r], "x", "$i$j")
                }
            }

            lp.constrait(Constrait.LEQ)

            requesters.forEachIndexed { i, requester ->
                requester.bids.forEachIndexed { j, bid ->
                    lp.term(bid.bundle[r], "y", "$i$j")
                }
            }

            lp.number(0.0)
            lp.newline()
        }
    }

    //予算制約
    fun writeSubToBudget(lp: LpWriter, obj: lpfile.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>, config: Config) {
        //全てのrequesterの入札について
        requesters.forEachIndexed { i, requester ->
            requester.bids.forEachIndexed { j, bid ->
                lp.constrateName("budget$i$j")
                bid.bundle.forEachIndexed { r, time ->
                    lp.term(bid.bundle[r], "y", "$i$j")

                }
            }
        }
    }


}
