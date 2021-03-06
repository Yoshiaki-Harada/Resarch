package winner

import config.Config
import cplex.lpformat.Constrait
import cplex.lpformat.Object
import cplex.lpformat.VarType
import model.Bidder
import writer.LpWriter
import java.io.BufferedWriter
import kotlin.math.max

/**
 * 利益最大化の定式化
 * 仮想的なQを考慮する
 */
class ProfitMaxPaddingDoubleAuction(val config: Config, val obj: Object, val bidders: List<Bidder>) : LpMaker {

    private val providers = bidders.subList(0, config.provider)
    private val requesters = bidders.subList(config.provider, config.provider + config.requester)
    val lp = LpWriter("${config.lpDir}/${config.lpFile}")
    val writer = lp.getBufferedWriter()
    private val q = getQ(config.resource)

    override fun makeLpFile() {
        writer.use {
            println("Q $q")
            //目的関数
            writeObjFunction(it)
            //制約条件
            lp.subto(it)
            writeSubToProvide(it)
            writeSubToRelationXsndY(it)
            writeSubToBidY(it)
            writeSubToQ(it)
            writeBinVariable(it)
            writeGeneralVariable(config.resource, it)
            lp.end(it)
        }
    }

    private fun getQ(resource: Int): List<Double> {
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
     *
     */
    private fun writeObjFunction() {
        lp.obj(obj)
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
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

        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                lp.minus(resource.getValue(), "q", "$i,$r")
            }
        }
        lp.newline()
    }


    private fun writeObjFunction(writer: BufferedWriter) {
        lp.obj(obj, writer)
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                lp.term(bid.getValue(), "y", "$j,$n", writer)
            }
        }
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jの入札nに提供する時間x(正の整数)
                        lp.minus(resource.getValue(), "x", "$i,$r,$j,$n", writer)
                        if ((i + r + j + n) % 20 == 0) lp.newline()
                    }
                }
            }
        }
        lp.newline(writer)

        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                lp.minus(resource.getValue(), "q", "$i,$r", writer)
            }
        }
        lp.newline(writer)
    }

    /**
     * 提供側の容量制約
     *  s.t. \sum_{j=1}^{J}\sum_{n=1}^{N} x_{i,r,j,n} + q_{i,r}
     * \leq TP_{i,r} (\forall i, \forall r)
     *
     */
    private fun writeSubToProvide() {
        //全てのresouceについて
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                lp.constrateName("provider times $i,$r")
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        lp.term("x", "$i,$r,$j,$n")
                    }
                }

                lp.plus()
                lp.variable("q", "$i,$r")

                lp.constrait(Constrait.LEQ)
                lp.number(resource.bundle[r])
                lp.newline()
            }
        }
    }

    private fun writeSubToProvide(writer: BufferedWriter) {
        //全てのresouceについて
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                lp.constrateName("provider times $i,$r", writer)
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        lp.term("x", "$i,$r,$j,$n", writer)
                    }
                }

                lp.plus(writer)
                lp.variable("q", "$i,$r", writer)

                lp.constrait(Constrait.LEQ, writer)
                lp.number(resource.bundle[r], writer)
                lp.newline(writer)
            }
        }
    }

    /**
     * \begin{cases}
     * x_{i,r,j,n} = 0  &({\rm if} \ y_{j,n}=0) \\
     * \sum_{i=1}^{I}\sum_{n=1}^{N} x_{i,r,j,n} \\ \quad \quad = TR_{j,n,r}
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
                        lp.constrateName("bundle,if 0,$i,$r,$j,$n")
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

    private fun writeSubToRelationXsndY(writer: BufferedWriter) {
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                //条件分岐
                //y_00=0の時はx_ir00=0でないといけない
                //y_00=1の時は[100, 50]
                //r[0]x_0000 + r[0]x_1000 >= 100
                //r[1]x_0100 + r[1]x_1100 >= 50
                providers.forEachIndexed { i, provider ->
                    provider.bids.forEachIndexed { r, resource ->
                        lp.constrateName("bundle,if 0,$i,$r,$j,$n", writer)
                        lp.variable("y", "$j,$n", writer)
                        lp.constrait(Constrait.EQ, writer)
                        lp.number(0.0, writer)
                        lp.arrow(writer)
                        lp.term("x", "$i,$r,$j,$n", writer)
                        lp.constrait(Constrait.EQ, writer)
                        lp.number(0.0, writer)
                        lp.newline(writer)
                    }
                }
                for (r in 0 until config.resource) {
                    lp.constrateName("bundle,if 1,$r,$j,$n", writer)
                    lp.variable("y", "$j,$n", writer)
                    lp.constrait(Constrait.EQ, writer)
                    lp.number(1.0, writer)
                    lp.arrow(writer)
                    providers.forEachIndexed { i, provider ->
                        //resource.bundle[r]
                        lp.term("x", "$i,$r,$j,$n", writer)
                    }
                    lp.constrait(Constrait.EQ, writer)
                    lp.number(bid.bundle[r], writer)
                    lp.newline(writer)
                }
            }
        }
    }


    /**
     * \sum_{i=1}^{I}q_{i,r}=Q_{r} (\forall r)
     *
     */
    private fun writeSubToQ() {
        for (r in 0 until config.resource) {
            lp.constrateName("Q$r")
            providers.forEachIndexed { i, requester ->
                lp.term("q", "$i,$r")
            }
            lp.constrait(Constrait.EQ)
            lp.number(q[r])
            lp.newline()
        }
        lp.newline()
    }

    private fun writeSubToQ(writer: BufferedWriter) {
        for (r in 0 until config.resource) {
            lp.constrateName("Q$r", writer)
            providers.forEachIndexed { i, requester ->
                lp.term("q", "$i,$r", writer)
            }
            lp.constrait(Constrait.EQ, writer)
            lp.number(q[r], writer)
            lp.newline(writer)
        }
        lp.newline(writer)
    }

    /**
     *  \sum_{n=1}^{N}y_{j,n}  \leq 1 \quad (\forall j)
     *
     */
    private fun writeSubToBidY() {
        requesters.forEachIndexed { j, requesters ->
            lp.constrateName("bidY$j")
            requesters.bids.forEachIndexed { n, bid ->
                lp.term("y", "$j,$n")
            }
            lp.constrait(Constrait.LEQ)
            lp.number(1.0)
            lp.newline()
        }
    }

    private fun writeSubToBidY(writer: BufferedWriter) {
        requesters.forEachIndexed { j, requesters ->
            lp.constrateName("bidY$j", writer)
            requesters.bids.forEachIndexed { n, bid ->
                lp.term("y", "$j,$n", writer)
            }
            lp.constrait(Constrait.LEQ, writer)
            lp.number(1.0, writer)
            lp.newline(writer)
        }
    }

    /**
     *  y_{j,n} \in  {0,1}
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

    private fun writeBinVariable(writer: BufferedWriter) {
        lp.varType(VarType.BIN, writer)
        requesters.forEachIndexed { j, requester ->
            requester.bids.forEachIndexed { n, bid ->
                lp.variable("y", "$j,$n", writer)
            }
        }
        lp.newline(writer)
    }

    /**
     * x_{i,r,j,n} \in Z
     *
     */
    private fun writeGeneralVariable(resource: Int) {
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

        providers.forEachIndexed { i, requester ->
            for (r in 0 until resource) {
                lp.variable("q", "$i,$r")

            }
        }
        lp.newline()
    }

    private fun writeGeneralVariable(resource: Int, writer: BufferedWriter) {
        lp.varType(VarType.GEN, writer)
        providers.forEachIndexed { i, provider ->
            provider.bids.forEachIndexed { r, resource ->
                requesters.forEachIndexed { j, requester ->
                    requester.bids.forEachIndexed { n, bid ->
                        //provider_iがresource_rをrequester_jに提供する時間を表す変数
                        lp.variable("x", "$i,$r,$j,$n", writer)
                        if ((i + r + j + n) % 20 == 0) lp.newline(writer)
                    }
                }
            }
        }
        lp.newline(writer)

        providers.forEachIndexed { i, requester ->
            for (r in 0 until resource) {
                lp.variable("q", "$i,$r", writer)

            }
        }
        lp.newline(writer)
    }

}
