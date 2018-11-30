import lpformat.Constrait
import lpformat.VarType
import lpformat.Object as Obj

class LPMaker(filname: String) {
    val lp = LpWriter(filname)
    fun makeLP() {
        //目的
        lp.obj(Obj.MAX)
        //入札作成                  　　　　　　　(財1の要求量, 財2の要求量)
        val bid00 = Bid(0.0, arrayListOf(arrayListOf(0.0, 0.0)))
        val bid01 = Bid(6.0, arrayListOf(arrayListOf(1.0, 0.0)))
        val bid02 = Bid(0.0, arrayListOf(arrayListOf(0.0, 1.0)))
        val bid03 = Bid(6.0, arrayListOf(arrayListOf(1.0, 1.0)))
        val bid10 = Bid(0.0, arrayListOf(arrayListOf(0.0, 0.0)))
        val bid11 = Bid(0.0, arrayListOf(arrayListOf(1.0, 0.0)))
        val bid12 = Bid(0.0, arrayListOf(arrayListOf(0.0, 1.0)))
        val bid13 = Bid(8.0, arrayListOf(arrayListOf(1.0, 1.0)))
        val bid20 = Bid(0.0, arrayListOf(arrayListOf(0.0, 0.0)))
        val bid21 = Bid(0.0, arrayListOf(arrayListOf(1.0, 0.0)))
        val bid22 = Bid(5.0, arrayListOf(arrayListOf(0.0, 1.0)))
        val bid23 = Bid(5.0, arrayListOf(arrayListOf(1.0, 1.0)))
        val bidder0 = Bidder()
        bidder0.add(bid00)
        bidder0.add(bid01)
        bidder0.add(bid02)
        bidder0.add(bid03)
        val bidder1 = Bidder()
        bidder1.add(bid10)
        bidder1.add(bid11)
        bidder1.add(bid12)
        bidder1.add(bid13)
        val bidder2 = Bidder()
        bidder2.add(bid20)
        bidder2.add(bid21)
        bidder2.add(bid22)
        bidder2.add(bid23)
        val bidders = arrayListOf<Bidder>()
        bidders.add(bidder0)
        bidders.add(bidder1)
        bidders.add(bidder2)

        for (bidder in bidders.withIndex()) {
            for (bid in bidder.value.bids.withIndex()) {
                //pxijと書いている
                lp.term(bid.value.price, "x", bidder.index.toString() + bid.index.toString())
                //最後は無視
                if (bidder.index == bidders.size-1 && bid.index  == bidder.value.bids.size -1)
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
                    for (bundle in bid.value.bundle)
                        lp.term(bundle[n], "x", bidder.index.toString() + bid.index.toString())
                    if (bidder.index == bidders.size-1 && bid.index  == bidder.value.bids.size -1)
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