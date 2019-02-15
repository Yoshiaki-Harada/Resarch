package winner

import config.Config
import cplex.lpformat.Constrait
import cplex.lpformat.Object
import model.Bidder
import model.Option
import writer.LpWriter

object CostMinPenaltyAuction : LpMaker {
    val penalty = 1000.0
    override fun makeLpFile(config: Config, obj: Object, bidders: List<Bidder>, vararg option: Option) {
        val lp = LpWriter(config.lpFile)
        val providers = bidders.subList(0, config.provider)
        val requesters = bidders.subList(config.provider, config.provider + config.requester)

        // 目的関数
        writeObjFunction(lp, obj, providers, requesters)
        // 提供時間制約
        writeSubToProvide(lp, obj, providers, requesters)

        writeSubToBidY(lp, obj, providers, requesters)

        writeSubToBidX(lp, obj, providers, requesters, config)
    }

    fun writeObjFunction(lp: LpWriter, obj: cplex.lpformat.Object, providers: List<Bidder>, requesters: List<Bidder>) {
        lp.obj(obj)
        lp.number(penalty)
        lp.leftBracket()
        requesters.forEachIndexed { j, requester ->
            lp.leftBracket()
            lp.number(1.0)
            lp.minus()
            requester.bids.forEachIndexed { n, bid ->
                lp.variable("y", "$j$n")
            }
            lp.rightBracket()
            if (j != requesters.size - 1)
                lp.plus()
        }
        lp.rightBracket()
        lp.plus()
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
        lp.rightBracket()
        lp.newline()
    }

    // 提供時間制約
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

    // 勝者となれるのは1企業のみ
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

    // 勝者となる入札はたかだか1つ
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

    // 勝者となる入札はたかだか1つ
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
}