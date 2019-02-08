package winner

import config.Config
import cplex.lpformat.Object
import model.Bidder
import model.Option
import writer.LpWriter
import cplex.lpformat.Constrait
import cplex.lpformat.VarType

object ProfitMaxDoubleAuction : LpMaker {
    override fun makeLpFile(config: Config, obj: Object, bidders: List<Bidder>, vararg option: Option) {
        val lp = LpWriter(config.lpFile)
        val providers = bidders.subList(0, config.provider)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)
        //目的関数
        writeObjFunction(lp, obj, providers, requesters)
        //制約条件
        lp.subto()
        writeSubToProvide(lp, obj, providers, requesters)
        writeSubToRelstionXsndY(lp, obj, providers, requesters, config)
        writeSubToWinner(lp, obj, providers, requesters)
        writeBinVariable(lp, providers, requesters)
        lp.end()
    }

    fun writeObjFunction(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>) {
        lp.obj(obj)
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                lp.term(bid.getValue(), "y", "$j$n")
            }
        }
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jに提供するとき1となる変数
                        //provider_iがresource_rをrequester_jの入札nに提供する時間x(正の整数)

                        lp.minus(-resource.getValue() * bid.bundle[r], "x", "$i$r$j$n")
                    }
                }
            }
        }
        lp.newline()
    }

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

    fun writeSubToRelstionXsndY(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>, config: Config) {
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                //条件分岐
                //y_00=0の時はx_ir00=0でないといけない
                //y_00=1の時は[100, 50]
                //r[0]x_0000 + r[0]x_1000 >= 100
                //r[1]x_0100 + r[1]x_1100 >= 50
                providers.forEachIndexed { i, provider ->
                    provider.bids.forEachIndexed { r, resource ->
                        lp.constrateName("bundle,0,$i,$r,$j,$n")
                        lp.variable("y", "$j$n")
                        lp.constrait(Constrait.EQ)
                        lp.number(0.0)
                        lp.arrow()
                        lp.term("x", "$i$r$j$n")
                        lp.constrait(Constrait.EQ)
                        lp.number(0.0)
                        lp.newline()
                    }
                }

                for (r in 0 until config.resource) {
                    lp.constrateName("bundle,1,$$r,$j,$n")
                    lp.variable("y", "$j$n")
                    lp.constrait(Constrait.EQ)
                    lp.number(1.0)
                    lp.arrow()
                    providers.forEachIndexed { i, provider ->
                        //resource.bundle[r]
                        lp.term(bid.bundle[r], "x", "$i$r$j$n")
                    }
                    lp.constrait(Constrait.EQ)
                    lp.number(bid.bundle[r])
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
    fun writeSubToBudget(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>) {
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

    fun writeBinVariable(lp: LpWriter, providers: List<Bidder>, requesters: List<Bidder>) {
        lp.varType(VarType.BIN)
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                lp.variable("y", "$j$n")
            }
        }
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
