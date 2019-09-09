package winner

import config.Config
import cplex.lpformat.Constrait
import cplex.lpformat.Object
import cplex.lpformat.VarType
import model.Bidder
import writer.LpWriter

/**
 * 利益最大化の定式化
 *         val lp = LpWriter("${config.lpDir}/${config.lpFile}")
 */
class ProfitMaxDoubleAuction(val config: Config, val obj: Object, val bidders: List<Bidder>) : LpMaker {

    val providers = bidders.subList(0, config.provider)
    val requesters = bidders.subList(config.provider, config.provider + config.requester)
    val lp = LpWriter("${config.lpDir}/${config.lpFile}")
    override fun makeLpFile() {
        //目的関数
        writeObjFunction()
        //制約条件
        lp.subto()
        writeSubToProvide()
        writeSubToRelationXsndY()
        writeSubToBidY()
        writeBinVariable()
        writeGeneralVariable()

        lp.end()
    }

    /**
     * 目的関数
     *  max \sum_{j=1}^{J}\sum_{n=1}^{N}v_{j} \times y_{j,n}
     *  -\sum_{i=1}^{I}\sum_{r=1}^{R}\sum_{j=1}^{J}\sum_{n=1}^{N}c_{i,r}\times
     *  TR_{i,n,r}\times x_{i,r,j,n}
     *
     */
    private fun writeObjFunction() {
        lp.obj(obj)
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                println("requester_$j,$n ${bid.getValue()}")
                lp.term(bid.getValue(), "y", "$j,$n")
            }
        }
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jの入札nに提供する時間x(正の整数)
                        lp.minus(resource.getValue(), "x", "$i,$r,$j,$n")
                        if ((i + r + j + n) % 20 == 0) lp.newline()
                    }
                }
            }
        }
        lp.newline()
    }

    /**
     * 提供側の容量制約
     *  s.t. \sum_{j=1}^{J}\sum_{n=1}^{N}TR_{j,n,r}  \times x_{i,r,j,n}
     * \leq TP_{i,r} (\forall i, \forall r)
     *
     */
    private fun writeSubToProvide() {
        //全てのresouceについて
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                lp.constrateName("provider $i,$r")
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        lp.term("x", "$i,$r,$j,$n")
                    }
                }
                lp.constrait(Constrait.LEQ)
                lp.number(resource.bundle[r])
                lp.newline()
            }
        }
    }

    /**
     * \begin{cases}
     * x_{i,r,j,n} = 0  &({\rm if} \ y_{j,n}=0) \\
     * \sum_{i=1}^{I}\sum_{n=1}^{N}  x_{i,r,j,n} \\ \quad \quad = TR_{j,n,r}
     * &({\rm if} \ y_{j,n}=1)
     * \end{cases}
     *
     */
    private fun writeSubToRelationXsndY() {
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
                        lp.variable("y", "$j,$n")
                        lp.constrait(Constrait.EQ)
                        lp.number(0.0)
                        lp.arrow()
                        lp.term("x", "$i,$r,$j,$n")
                        lp.constrait(Constrait.EQ)
                        lp.number(0.0)
                        lp.newline()
                    }
                }

                for (r in 0 until config.resource) {
                    lp.constrateName("bundle,if 1,$r,$j,$n")
                    lp.variable("y", "$j,$n")
                    lp.constrait(Constrait.EQ)
                    lp.number(1.0)
                    lp.arrow()
                    providers.forEachIndexed { i, provider ->
                        //resource.bundle[r]
                        lp.term("x", "$i,$r,$j,$n")
                    }
                    lp.constrait(Constrait.EQ)
                    lp.number(bid.bundle[r])
                    lp.newline()
                }
            }
        }
    }

    /**
     *  \sum_{n=1}^{N}y_{j,n}  \leq 1 \quad (\forall j)
     *
     */
    private fun writeSubToBidY() {
        requesters.forEachIndexed { j, requesters ->
            lp.constrateName("bidY $j")
            requesters.bids.forEachIndexed { n, bid ->
                lp.term("y", "$j,$n")
            }
            lp.constrait(Constrait.LEQ)
            lp.number(1.0)
            lp.newline()
        }
    }

    /**
     *   x_{i,r,j,n},y_{j,n} \in {0,1}
     *
     */
    private fun writeBinVariable() {
        lp.varType(VarType.BIN)
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                lp.variable("y", "$j,$n")
            }
        }
        lp.newline()
    }

    /**
     * x_{i,r,j,n} \in Z
     *
     */
    private fun writeGeneralVariable() {
        lp.varType(VarType.GEN)
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jに提供する時間を表す変数
                        lp.variable("x", "$i,$r,$j,$n")
                        if ((i + r + j + n) % 20 == 0) lp.newline()
                    }
                }
            }
        }

        lp.newline()
    }
}
