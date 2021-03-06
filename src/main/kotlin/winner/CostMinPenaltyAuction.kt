package winner

import config.Config
import cplex.lpformat.Constrait
import cplex.lpformat.Object
import cplex.lpformat.VarType
import model.Bidder
import writer.LpWriter

/**
 * コスト最小化-penaltyの定式化
 * TODO 整数変数yの導入
 */
class CostMinPenaltyAuction(val config: Config, val obj: Object, val bidders: List<Bidder>) : LpMaker {

    override fun makeLpFile() {
        val providers = bidders.subList(0, config.provider)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)
        val lp = LpWriter("${config.lpDir}/${config.lpFile}")

        // 目的関数
        writeObjFunction(lp, obj, providers, requesters, config)
        lp.subto()

        writeSubToRelstionXsndY(lp, obj, providers, requesters, config)
        // 提供時間制約
        writeSubToProvide(lp, obj, providers, requesters)

        //  writeSubToWinner(lp, obj, providers, requesters)

        writeSubToBidY(lp, obj, providers, requesters)

        writeSubToBidX(lp, obj, providers, requesters, config)

        //予算制約
        writeSubToBudget(lp, obj, providers, requesters, config)

        writeBinVariable(lp, providers, requesters)

        lp.end()

    }

    /**
     * 目的関数
     *
     * @param lp
     * @param obj
     * @param providers
     * @param requesters
     * @param config
     */
    fun writeObjFunction(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>, config: Config) {
        lp.obj(obj)

        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                lp.minus(config.penalty, "y", "$j$n")
            }
        }
        lp.newline()
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jに提供するとき1となる変数
                        //provider_iがresource_rをrequester_jの入札の要求resource_mに提供する時間x(正の整数)
                        lp.term(resource.getValue() * bid.bundle[r], "x", "$i$r$j$n")
                        if ((i + r + j + n) % 20 == 0) lp.newline()
                    }
                }
            }
        }
        lp.newline()
    }

    /**
     * 提供側の容量制約
     *
     * @param lp
     * @param obj
     * @param providers
     * @param requesters
     */
    fun writeSubToProvide(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>) {
        // 全てのresouceについて
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

    /**
     * 勝者となれるのは1企業のみ
     *
     * @param lp
     * @param obj
     * @param providers
     * @param requesters
     */
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

    /**
     * 勝者となる入札はたかだか1つ
     *
     * @param lp
     * @param obj
     * @param providers
     * @param requesters
     */
    fun writeSubToBidY(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>) {
        requesters.forEachIndexed { j, requesters ->
            lp.constrateName("bidY $j")
            requesters.bids.forEachIndexed { n, bid ->
                lp.term("y", "$j$n")
            }
            lp.constrait(Constrait.LEQ)
            lp.number(1.0)
            lp.newline()
        }
    }

    fun writeSubToRelstionXsndY(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>, config: Config) {
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                //条件分岐
                //y_00=0の時はx_ir00=0でないといけない
                //y_00=1の時は[100, 50]
                //r[0]x_0000 + r[0]x_1000 = 100
                //r[1]x_0100 + r[1]x_1100 = 50
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

    /**
     * 勝者となる入札はたかだか1つ
     *
     * @param lp
     * @param obj
     * @param providers
     * @param requesters
     * @param config
     */
    fun writeSubToBidX(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>, config: Config) {
        providers.forEachIndexed { i, provider ->
            for (r in 0..config.resource) {
                requesters.forEachIndexed { j, requester ->
                    lp.constrateName("bidX $i,$r,$j")
                    provider.bids.forEachIndexed { n, bid ->
                        lp.term("x", "$i$r$j$n")
                    }
                    lp.constrait(Constrait.LEQ)
                    lp.number(1.0)
                    lp.newline()
                }
            }
        }
    }

    /**
     * 予算制約
     *
     * @param lp
     * @param obj
     * @param providers
     * @param requesters
     * @param config
     */
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

    /**
     * 0-1変数
     *
     * @param lp
     * @param providers
     * @param requesters
     */
    fun writeBinVariable(lp: LpWriter, providers: List<Bidder>, requesters: List<Bidder>) {
        lp.varType(VarType.BIN)
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                lp.variable("y", "$j$n")
            }
        }
        lp.newline()
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jに提供するとき1となる変数
                        lp.variable("x", "$i$r$j$n")
                        if ((i + r + j + n) % 20 == 0) lp.newline()
                    }
                }
            }
        }
        lp.newline()
    }


}