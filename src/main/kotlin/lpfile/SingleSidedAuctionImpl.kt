package lpfile

import config.Config
import lpfile.lpformat.Constrait
import lpfile.lpformat.VarType
import model.Bidder
import model.Option
import model.Resource
import writer.LpWriter
import auction.SingleSidedAuction
import lpfile.lpformat.Object as Obj


/*ファイル名とAllayList<入札者>を受け取って定式化を行いLPファイルを作成する*/
object SingleSidedAuctionImpl : LpMaker, SingleSidedAuction {

    override fun makeLpFile(config: Config, obj: lpfile.lpformat.Object, bidders: List<Bidder>, vararg options: Option) {
        when (options.size) {
            1 -> {
                val option = options[0]
                when (option) {
                    is Resource -> {
                        val resource = option.time
                        val lp = LpWriter(config.lpFile)
                        //目的
                        writeObjFunction(lp, obj, bidders)
                        //制約条件
                        writeSubTo(lp, bidders, resource)
                        //0-1変数制約
                        writeBinVariable(lp, bidders)
                        //終了
                        lp.end()
                    }
                }
            }
            else -> {
                println("option-resource が正しくありません")
                return
            }
        }
    }

    fun writeObjFunction(lp: LpWriter, obj: lpfile.lpformat.Object, bidders: List<Bidder>) {
        lp.obj(obj)
        kotlin.run loop@{
            bidders.forEachIndexed { i, bidder ->
                bidder.bids.forEachIndexed { j, bid ->
                    lp.term(bid.getValue(), "x", i.toString() + j.toString())
                    if (i == bidders.size - 1 && j == bidder.bids.size - 1)
                        return@loop
                    lp.plus()
                }
            }
        }
        lp.newline()
    }

    fun writeSubTo(lp: LpWriter, bidders: List<Bidder>, resource: Array<Double>) {
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
    }

    fun writeBinVariable(lp: LpWriter, bidders: List<Bidder>) {
        lp.varType(VarType.BIN)
        bidders.forEachIndexed { i, bidder ->
            bidder.bids.forEachIndexed { j, _ ->
                lp.term("x", i.toString() + j.toString())
            }
        }
        lp.newline()
    }


}