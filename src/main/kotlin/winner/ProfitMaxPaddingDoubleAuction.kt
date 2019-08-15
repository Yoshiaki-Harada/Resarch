package winner

import config.Config
import cplex.lpformat.Constrait
import cplex.lpformat.Object
import cplex.lpformat.VarType
import model.Bidder
import model.Option
import writer.LpWriter
import kotlin.math.max

/**
 * 利益最大化の定式化
 * 仮想的なQを考慮する
 */
object ProfitMaxPaddingDoubleAuction : LpMaker {
    override fun makeLpFile(config: Config, obj: Object, bidders: List<Bidder>, vararg option: Option) {
        val lp = LpWriter("${config.lpDir}/${config.lpFile}")
        val providers = bidders.subList(0, config.provider)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)
        val q = getQ(providers, requesters, config.resource)
        //目的関数
        writeObjFunction(lp, obj, providers, requesters, config.resource)
        //制約条件
        lp.subto()
        writeSubToProvide(lp, obj, providers, requesters, q)
        writeSubToRelationXsndY(lp, obj, providers, requesters, config)
        writeSubToBidY(lp, obj, providers, requesters)
        writeSubToQ(lp, providers, requesters, q, config)
        writeBinVariable(lp, providers, requesters)
        writeGeneralVariable(lp, providers, requesters, config.resource)
        lp.end()
    }

    private fun getQ(providers: List<Bidder>, requesters: List<Bidder>, resource: Int): List<Double> {
        val requesterQ = List(resource) { r ->
            requesters.map { requester ->
                requester.bids.map { bid ->
                    bid.bundle[r]
                }.max() ?: 0.0
            }.max() ?: 0.0
        }

        val providerQ = List(resource) { r ->
            providers.map { provider ->
                provider.bids.map { bid ->
                    bid.bundle[r]
                }.max() ?: 0.0
            }.max() ?: 0.0
        }
        return List(resource) { r ->
            max(providerQ[r], requesterQ[r])
        }
    }

    /**
     * 目的関数
     *  max \sum_{j=1}^{J}\sum_{n=1}^{N}v_{j} \times y_{j,n}
     *  -\sum_{i=1}^{I}\sum_{r=1}^{R}\sum_{j=1}^{J}\sum_{n=1}^{N}c_{i,r}\times
     *  TR_{i,n,r}\times x_{i,r,j,n}
     *  -\sum_{i=1}^{I}\sum_{r=1}^{R}c_{i,r}q_{i,r}
     * @param lp
     * @param obj
     * @param providers
     * @param requesters
     */
    fun writeObjFunction(lp: LpWriter, obj: Object, providers: List<Bidder>, requesters: List<Bidder>, resource: Int) {
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
                        //provider_iがresource_rをrequester_jの入札nに提供する時間x(正の整数)
                        lp.minus(resource.getValue(), "x", "$i$r$j$n")
                        if ((i + r + j + n) % 20 == 0) lp.newline()
                    }
                }
            }
        }
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                lp.minus(resource.getValue(), "q", "$i$r")
            }
        }
        lp.newline()
    }

    /**
     * 提供側の容量制約
     *  s.t. \sum_{j=1}^{J}\sum_{n=1}^{N} x_{i,r,j,n} + q_{i,r}
     * \leq TP_{i,r} (\forall i, \forall r)
     *
     * @param lp
     * @param obj
     * @param providers
     * @param requesters
     */
    fun writeSubToProvide(lp: LpWriter, obj: Object, providers: List<Bidder>, requesters: List<Bidder>, q: List<Double>) {
        //全てのresouceについて
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                lp.constrateName("provider times $i,$r")
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        lp.term("x", "$i$r$j$n")
                    }
                }

                lp.plus()
                lp.variable("q", "$i$r")

                lp.constrait(Constrait.LEQ)
                lp.number(resource.bundle[r])
                lp.newline()
            }
        }
    }

    /**
     * \begin{cases}
     * x_{i,r,j,n} = 0  &({\rm if} \ y_{j,n}=0) \\
     * \sum_{i=1}^{I}\sum_{n=1}^{N} x_{i,r,j,n} \\ \quad \quad = TR_{j,n,r}
     * &({\rm if} \ y_{j,n}=1)
     * \end{cases}

     * @param lp
     * @param obj
     * @param providers
     * @param requesters
     * @param config
     */
    fun writeSubToRelationXsndY(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>, config: Config) {
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                //条件分岐
                //y_00=0の時はx_ir00=0でないといけない
                //y_00=1の時は[100, 50]
                //r[0]x_0000 + r[0]x_1000 >= 100
                //r[1]x_0100 + r[1]x_1100 >= 50
                providers.forEachIndexed { i, provider ->
                    provider.bids.forEachIndexed { r, resource ->
                        lp.constrateName("bundle,if 0,$i,$r,$j,$n")
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
                    lp.constrateName("bundle,if 1,$r,$j,$n")
                    lp.variable("y", "$j$n")
                    lp.constrait(Constrait.EQ)
                    lp.number(1.0)
                    lp.arrow()
                    providers.forEachIndexed { i, provider ->
                        //resource.bundle[r]
                        lp.term("x", "$i$r$j$n")
                    }
                    lp.constrait(Constrait.EQ)
                    lp.number(bid.bundle[r])
                    lp.newline()
                }
            }
        }
    }


    /**
     * \sum_{i=1}^{I}q_{i,r}=Q_{r} (\forall r)
     *
     * @param lp
     * @param providers
     * @param requesters
     * @param config
     */
    fun writeSubToQ(lp: LpWriter, providers: List<Bidder>, requesters: List<Bidder>, q: List<Double>, config: Config) {
        for (r in 0 until config.resource) {
            lp.constrateName("Q$r")
            providers.forEachIndexed { i, requester ->
                lp.term("q", "$i$r")
            }
            lp.constrait(Constrait.EQ)
            lp.number(q[r])
            lp.newline()
        }
        lp.newline()
    }

    /**
     *  \sum_{n=1}^{N}y_{j,n}  \leq 1 \quad (\forall j)
     *
     * @param lp
     * @param obj
     * @param providers
     * @param requesters
     */
    fun writeSubToBidY(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>) {
        requesters.forEachIndexed { j, requesters ->
            lp.constrateName("bidY$j")
            requesters.bids.forEachIndexed { n, bid ->
                lp.term("y", "$j$n")
            }
            lp.constrait(Constrait.LEQ)
            lp.number(1.0)
            lp.newline()
        }
    }

    /**
     *  y_{j,n} \in  {0,1}
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
    }

    /**
     * x_{i,r,j,n} \in Z
     *
     * @param lp
     * @param requesters
     */
    fun writeGeneralVariable(lp: LpWriter, providers: List<Bidder>, requesters: List<Bidder>, resource: Int) {
        lp.varType(VarType.GEN)
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jに提供する時間を表す変数
                        lp.variable("x", "$i$r$j$n")
                        if ((i + r + j + n) % 20 == 0) lp.newline()
                    }
                }
            }
        }
        providers.forEachIndexed { i, requester ->
            for (r in 0 until resource) {
                lp.variable("q", "$i$r")

            }
        }
        lp.newline()
    }

}