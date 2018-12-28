package lpfile

import config.Config
import lpfile.lpformat.Object
import model.Bidder
import model.Option
import writer.LpWriter
import auction.DoubleAuction
import lpfile.lpformat.Constrait

object CostMinDoubleAuction : LpMaker, DoubleAuction {

    override fun makeLpFile(config: Config, obj: Object, bidders: List<Bidder>, vararg option: Option) {
        val lp = LpWriter(config.lpFile)
        val providers = bidders.subList(0, config.provider)
        val requesters = bidders.subList(config.provider, config.requester)
        //目的関数
        writeObjFunction(lp, obj, providers)
        //制約条件
        //要求<=提供
        writeSubToTime(lp, obj, bidders, config)
        //予算制約


    }

    fun writeObjFunction(lp: LpWriter, obj: lpfile.lpformat.Object, providers: List<Bidder>) {
        lp.obj(obj)
        kotlin.run loop@{
            providers.forEachIndexed { i, provider ->
                provider.bids.forEachIndexed { j, bid ->
                    lp.term(bid.getValue(), "x", i.toString() + j.toString())
                    if (i == providers.size - 1 && j == provider.bids.size - 1)
                        return@loop
                    lp.plus()
                }
            }
        }
        lp.newline()
    }

    fun writeSubToTime(lp: LpWriter, obj: lpfile.lpformat.Object, bidders: List<Bidder>, config: Config) {
        //全てのresouceについて
        for (r in 0 until config.resource) {
            lp.constrateName("c" + r.toString())
            kotlin.run loop@{
                bidders.forEachIndexed { i, bidder ->
                    bidder.bids.forEachIndexed { j, bid ->
                        when {
                            //providerのとき
                            i < config.provider -> {
                                lp.term(bid.bundle[r], "x", i.toString() + j.toString())
                                lp.plus()
                            }
                            //requesterのとき
                            i >= config.provider -> {
                                lp.term(bid.bundle[r], "y", i.toString() + j.toString())
                            }
                        }
                    }
                }
            }
            lp.constrait(Constrait.LEQ)

            lp.number(0.0)
            lp.newline()
        }
    }

    fun writeSubToBudget(lp: LpWriter, obj: lpfile.lpformat.Object, bidders: List<Bidder>, config: Config) {
        //全てのresouceについて
        bidders.forEachIndexed { i, requester -> }
    }


}
