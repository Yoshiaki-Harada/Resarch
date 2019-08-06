import config.Config
import cplex.lpformat.Object
import ilog.concert.IloLPMatrix
import impoter.LpImporter
import model.Bid
import model.Bidder
import model.Value
import trade.padding_method.isOne
import winner.ProfitMaxDoubleAuction
import winner.ProfitMaxPaddingDoubleAuction

fun main(args: Array<String>) {

    val config = Config.fromJson("config-test")

    val bids0 = listOf<Bid>(
            Bid(Value(1.0, 0.0), listOf(2.0, 0.0)),
            Bid(Value(0.0, 0.0), listOf(0.0, 0.0)))

    val bids1 = listOf<Bid>(
            Bid(Value(2.0, 0.0), listOf(1.0, 0.0)),
            Bid(Value(0.0, 0.0), listOf(0.0, 0.0)))

    val bids2 = listOf<Bid>(
            Bid(Value(4.0, 0.0), listOf(1.0, 0.0)),
            Bid(Value(0.0, 0.0), listOf(0.0, 0.0)))

    val bids3 = listOf<Bid>(
            Bid(Value(0.0, 0.0), listOf(0.0, 0.0)),
            Bid(Value(0.0, 1.0), listOf(0.0, 1.0)))

    val bids4 = listOf<Bid>(
            Bid(Value(0.0, 0.0), listOf(0.0, 0.0)),
            Bid(Value(0.0, 1.0), listOf(0.0, 1.0)))

    val provider0 = Bidder().add(bids0)
    val provider1 = Bidder().add(bids1)
    val provider2 = Bidder().add(bids2)
    val provider3 = Bidder().add(bids3)
    val provider4 = Bidder().add(bids4)

    val p1 = listOf<Bid>(
            Bid(Value(9.0, 0.0), listOf(2.0, 0.0)),
            Bid(Value(3.0, 0.0), listOf(0.0, 1.0)))
    val requester0 = Bidder().add(p1)

    val bidders = listOf(provider0, provider1, provider2, provider3, provider4, requester0)

    ProfitMaxPaddingDoubleAuction.makeLpFile(config, Object.MAX, bidders)

    val cplex = LpImporter("LP/test").getCplex()

    cplex.solve()

    val status = cplex.status
    println("status = $status")
    // 目的関数値
    val objValue = cplex.objValue
    println("objValue = $objValue")

    val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
    val cplexValue = cplex.getValues(lp)

    val providers = bidders.subList(0, config.provider)
    println("providerNumber:" + providers.size)
    val requesters = bidders.subList(config.provider, config.provider + config.requester)
    println("requesterNumber:" + requesters.size)

    val sum = requesters.map { it.bids.size }.sum()
    val tempY = cplexValue.copyOfRange(0, sum)
    val y = Util.convertDimension(tempY, requesters.map { it.bids.size })
    val excludedXCplex = cplexValue.copyOfRange(sum, cplexValue.lastIndex - config.resource + 1)
    val x = Util.convertDimension4(excludedXCplex, requesters.map { it.bids.size }, providers.map { it.bids.size }, config)
    val tempQ = cplexValue.copyOfRange(cplexValue.lastIndex + 1 - config.provider * config.resource, cplexValue.lastIndex + 1)
    val q = Util.convertDimension(tempQ, List(providers.size) { config.resource })

    List(requesters.size) { config.resource }

    y.forEachIndexed { index, doubles ->
        println("y$index=${doubles.toList()}")
    }

    x.forEachIndexed { i, provider ->
        provider.forEachIndexed { r, resource ->
            resource.forEachIndexed { j, requester ->
                requester.forEachIndexed { n, d ->
                    print("x_$i$r$j$n = $d, ")
                }
            }
        }
        println()
    }

    q.forEach {
        println(it.toList())
    }

    // 要求側の価格決定

    y.forEachIndexed { j, bids ->
        bids.forEachIndexed { n, d ->
            if (isOne(d)) {
                // 支払い価格を求める
                requesters.filter { requester ->
                    requester.id != j
                }.let { excludedRequesters ->

                    // iを除いたオークションの目的関数の値を得る
                    println("determination $j 's payment")
                    config.lpFile = "Padding/auction\\{$j}"
                    config.requester = config.requester - 1
                    val bidders = providers.plus(excludedRequesters)
                    ProfitMaxPaddingDoubleAuction.makeLpFile(config, Object.MAX, bidders)
                    val cplex = LpImporter("LP/Padding/auction\\{$j}").getCplex()
                    cplex.solve()

                    // 支払い価格を導出する
                    println("${requesters[j].bids[n].getValue()}, ${cplex.objValue} $objValue")
                    val pay = requesters[j].bids[n].getValue() - (objValue - cplex.objValue)
                    println("requester$j,$n payment = $pay")
                }
            }
        }
    }

    // 提供側の報酬決定
    val wins = mutableListOf<Bidder>()
    y.forEachIndexed { j, bids ->
        bids.forEachIndexed { n, d ->
            if (isOne(d)) {
                wins.add(Bidder(requesters[j].bids[n]))
            }
        }
    }

    val winRequesters = wins as List<Bidder>
    //configの書き直し
    config.lpFile = "Padding/winRequester"
    config.requester = winRequesters.size

    ProfitMaxDoubleAuction.makeLpFile(config, Object.MAX, providers.plus(winRequesters))
    val cplex2 = LpImporter("LP/Padding/winRequeste").getCplex()

    cplex2.solve()

    val lp2 = cplex.LPMatrixIterator().next() as IloLPMatrix
    val cplexValue2 = cplex.getValues(lp2)
    val tempY2 = cplexValue2.copyOfRange(0, winRequesters.map { it.bids.size }.sum())
    val y2 = Util.convertDimension(tempY2, requesters.map { it.bids.size })
    val excludedXCplex2 = cplexValue.copyOfRange(winRequesters.map { it.bids.size }.sum(), cplexValue.lastIndex - config.resource + 1)
    val x2 = Util.convertDimension4(excludedXCplex2, winRequesters.map { it.bids.size }, providers.map { it.bids.size }, config)
    val tempQ2 = cplexValue.copyOfRange(cplexValue2.lastIndex + 1 - config.provider * config.resource, cplexValue2.lastIndex + 1)
    val q2 = Util.convertDimension(tempQ2, List(providers.size) { config.resource })

    data class A(val a: String)
    // requester's r' sum d
    // 決定変数が1の時に取引を行う
    x2.forEachIndexed { i, provider ->
        provider.forEachIndexed { r, resource ->
            val quantity = resource.map { requester ->
                requester.sum()
            }.sum()

        }
    }
}

