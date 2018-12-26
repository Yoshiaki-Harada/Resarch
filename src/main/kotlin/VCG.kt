import Impoter.LpImporter
import config.Config
import converter.ConfigConverter
import converter.ResultConverter
import ilog.concert.IloLPMatrix
import lpformat.Object
import model.Bid
import model.Bidder
import result.BidResult
import result.Result
import writer.JsonWriter

class VCG() {
    fun start(lpfile: String, bidders: MutableList<Bidder>, resource: Array<Double>, config: Config) {
        val cplex = LpImporter(lpfile).getCplex()
        cplex.solve()
        //最適かの判定
        val status = cplex.status
        //目的関数値
        val objValue = cplex.objValue
        val lp = cplex.LPMatrixIterator().next() as IloLPMatrix
        val xCplex = cplex.getValues(lp)

        //入札を読み込む
        //xを二次元配列へと変換
        val x = Util.convertDimension(xCplex, bidders.map { it.bids.size })

        //勝者となった入札を読み込む
        val winBids = getWinBids(x)
        //勝者となった入札を除いた入札を作成する
        val bidResults: MutableList<BidResult> = mutableListOf()


        winBids.forEach { bidIndex ->
            val exbidders = bidders.mapIndexed() { i, bidder ->
                Bidder().add(bidder.bids.filterIndexed { j, bid ->
                    !(bidIndex[0] == i && bidIndex[1] == j)
                })
            }.toMutableList()

            val filename = "LP/VCG/Exclude" + bidIndex.toString()
            LPMaker(filename, Object.MAX, exbidders, resource).makeFile()
            val vcgCplex = LpImporter(filename).getCplex()
            vcgCplex.solve()
            val payment = vcgCplex.objValue - (objValue - bidders[bidIndex[0]].bids[bidIndex[1]].value)
            bidResults.add(BidResult(arrayOf(bidIndex[0], bidIndex[1]), payment))
        }

//
//        winBids.forEach { bidIndex ->
//            val exbidders: MutableList<Bidder> = mutableListOf()
//            val tmpBidder: Bidder = Bidder()
//            bidders.forEachIndexed { i, bidder ->
//                val tmpBids: MutableList<Bid> = mutableListOf()
//                bidder.bids.forEachIndexed { j, bid ->
//                    if (!(bidIndex[0] == i && bidIndex[1] == j)) {
//                        tmpBids.add(bid)
//                    }
//                }
//                tmpBidder.add(tmpBids)
//                exbidders.add(tmpBidder)
//            }
//            val filename = "LP/VCG/Exclude" + bidIndex.toString()
//            LPMaker(filename, Object.MAX, exbidders, resource).makeFile()
//            val vcgCplex = LpImporter(filename).getCplex()
//            vcgCplex.solve()
//            val payment = vcgCplex.objValue - (objValue - bidders[bidIndex[0]].bids[bidIndex[1]].value)
//            bidResults.add(BidResult(arrayOf(bidIndex[0], bidIndex[1]), payment))
//        }


        JsonWriter(config.resultFile).makeFile(ResultConverter.toJson(Result(objValue, bidResults)))

        cplex.end()
    }

    fun getWinBids(x: List<DoubleArray>): List<List<Int>> {
        val winBids: MutableList<List<Int>> = mutableListOf()
        x.forEachIndexed { i, doubles ->
            doubles.forEachIndexed { j, d ->
                if (d == 1.0) {
                    winBids.add(listOf(i, j))
                }
            }
        }
        return winBids
    }
}