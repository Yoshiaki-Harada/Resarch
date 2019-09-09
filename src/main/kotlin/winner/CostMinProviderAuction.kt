package winner

import config.Config
import cplex.lpformat.Constrait
import cplex.lpformat.Object
import cplex.lpformat.VarType
import model.Bidder
import writer.LpWriter

/**
 * ペナルティではなく制約としての定式化
 */
class CostMinProviderAuction(val config: Config, val obj: Object, val bidders: List<Bidder>) : LpMaker {
    override fun makeLpFile() {
        val lp = LpWriter("${config.lpDir}/${config.lpFile}")
        val providers = bidders.subList(0, config.provider)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)
        //目的関数
        writeObjFunction(lp, obj, providers, requesters)
        //制約条件
        lp.subto()
        writeSubToProvide(lp, obj, providers, requesters)
        writeSubToRequest(lp, obj, providers, requesters)
        writeSubToWinner(lp, obj, providers, requesters)
        writeSubToBudget(lp, obj, providers, requesters, config)
        //0-1
        writeBinVariable(lp, providers, requesters)

        lp.end()
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

    //提供時間制約
    fun writeSubToProvide(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>) {
        //全てのresouceについて
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                lp.constrateName("provider $i,$r")
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        lp.term(bid.bundle[r], "x", "$i$r$j$n")
                    }
                }
                lp.constrait(Constrait.LEQ)
                lp.number(resource.bundle[r])
                lp.newline()
            }
        }
    }

    //要求制約
    fun writeSubToRequest(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>) {
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                bid.bundle.forEachIndexed { r, time ->
                    lp.constrateName("request $j,$n,$r")

                    providers.forEachIndexed { i, provider ->
                        lp.term(time, "x", "$i$r$j$n")
                    }

                    lp.constrait(Constrait.EQ)

                    lp.number(time)
                    lp.newline()
                }
            }
        }
    }

    //勝者となれるのは1企業のみ
    fun writeSubToWinner(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>) {
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                bid.bundle.forEachIndexed { r, time ->
                    lp.constrateName("winner $j,$n,$r")
                    providers.forEachIndexed { i, provider ->
                        lp.term("x", "$i$r$j$n")
                    }
                    lp.constrait(Constrait.LEQ)
                    lp.number(1.0)
                    lp.newline()
                }
            }
        }
    }


    //予算制約
    fun writeSubToBudget(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>, config: Config) {
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                lp.constrateName("budget $j,$n")
                providers.forEachIndexed { i, provider ->
                    provider.bids.forEachIndexed { r, resource ->
                        lp.term(resource.getValue() * bid.bundle[r], "x", "$i$r$j$n")
                    }
                }
                lp.constrait(Constrait.LEQ)
                lp.number(bid.getValue())
                lp.newline()
            }
        }
    }

    fun writeBinVariable(lp: LpWriter, providers: List<Bidder>, requesters: List<Bidder>) {
        lp.varType(VarType.BIN)
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jに提供するとき1となる変数
                        lp.variable("x", "$i$r$j$n")
                    }
                }
            }
        }
        lp.newline()
    }

}