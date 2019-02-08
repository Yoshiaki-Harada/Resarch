package winner

import config.Config
import cplex.lpformat.Object
import model.Bidder
import model.Option
import writer.LpWriter

object CostMinPenaltyAuction : LpMaker {
    override fun makeLpFile(config: Config, obj: Object, bidders: List<Bidder>, vararg option: Option) {
        val lp = LpWriter(config.lpFile)
        val providers = bidders.subList(0, config.provider)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)

    }

    fun writeObjFunction(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>) {
        lp.obj(obj)
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jに提供するとき1となる変数
                        //provider_iがresource_rをrequester_jの入札の要求resource_mに提供する時間x(正の整数)
                        lp.term(resource.getValue() * bid.bundle[r], "x", "$i$r$j$n")
                    }
                }
            }
        }
        lp.newline()
    }
}