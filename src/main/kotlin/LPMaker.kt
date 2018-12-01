import lpformat.Constrait
import lpformat.VarType
import lpformat.Object as Obj

class LPMaker(filname: String, val bidders :ArrayList<Bidder>) {
    val lp = LpWriter(filname)
    fun makeFile() {
        //目的
        lp.obj(Obj.MAX)

        for (bidder in bidders.withIndex())
            JsonWriter("Bid/Bidder"+bidder.index.toString()).makeFile((bidder.value.toJson()))

        for (bidder in bidders.withIndex()) {
            for (bid in bidder.value.bids.withIndex()) {
                //pxijと書いている
                lp.term(bid.value.price, "x", bidder.index.toString() + bid.index.toString())
                //最後は無視
                if (bidder.index == bidders.size - 1 && bid.index == bidder.value.bids.size - 1)
                    break
                lp.plus()
            }
        }
        lp.newline()
        //制約条件
        lp.subto()
        for (n in 0 until 2) {
            //制約の名前
            lp.constrateName("c" + n.toString())
            //各入札者について
            for (bidder in bidders.withIndex()) {
                //各入札について
                for (bid in bidder.value.bids.withIndex()) {
                    //財の組合せの(財1の要求量, 財2の要求量)
                    lp.term(bid.value.bundle[n], "x", bidder.index.toString() + bid.index.toString())
                    if (bidder.index == bidders.size - 1 && bid.index == bidder.value.bids.size - 1)
                        break
                    lp.plus()
                }
            }
            lp.constrait(Constrait.LEQ)
            lp.number(1.0)
            lp.newline()
        }
        lp.newline()

        //0-1変数制約
        lp.varType(VarType.BIN)
        for (bidder in bidders.withIndex()) {
            for (bid in bidder.value.bids.withIndex()) {
                lp.term("x", bidder.index.toString() + bid.index.toString())
            }
        }
        lp.newline()
        lp.end()
    }

}