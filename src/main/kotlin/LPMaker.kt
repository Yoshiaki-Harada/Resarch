import lpformat.Constrait
import lpformat.VarType
import model.Bidder
import writer.LpWriter
import lpformat.Object as Obj


/*ファイル名とAllayList<入札者>を受け取って定式化を行いLPファイルを作成する*/
class LPMaker(val filname: String, val obj: lpformat.Object, val bidders: MutableList<Bidder>, val resource: Array<Double>) {
    val lp = LpWriter(filname)
    fun makeFile() {
        //目的
        lp.obj(obj)
        kotlin.run loop@{
            bidders.forEachIndexed { i, bidder ->
                bidder.bids.forEachIndexed { j, bid ->
                    lp.term(bid.value, "x", i.toString() + j.toString())
                    if (i == bidders.size - 1 && j == bidder.bids.size - 1)
                        return@loop
                    lp.plus()
                }
            }
        }

        lp.newline()
        //制約条件
        lp.subto()

        resource.forEachIndexed { n, _ ->
            lp.constrateName("c" + n.toString())
            kotlin.run loop@{
                bidders.forEachIndexed { i, bidder ->
                    bidder.bids.forEachIndexed { j, bid ->
                        lp.term(bid.bundle[n], "x", i.toString() + j.toString())
                        if (i == bidders.size - 1 && j == bidder.bids.size - 1)
                            return@loop
                        lp.plus()
                    }
                }
            }
            lp.constrait(Constrait.LEQ)
            lp.number(resource[n])
            lp.newline()
        }

        lp.newline()
        lp.varType(VarType.BIN)

        //0-1変数制約
        bidders.forEachIndexed { i, bidder ->
            bidder.bids.forEachIndexed { j, _ ->
                lp.term("x", i.toString() + j.toString())
            }
        }

        lp.newline()
        lp.end()
    }

}