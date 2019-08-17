package trade

import impoter.LpImporter
import Util
import config.Config
import ilog.concert.IloLPMatrix
import model.Bidder
import model.Resource
import result.BidResult

object VCG  {
    fun start(lpfile: String, bidders: MutableList<Bidder>, resource: Resource, config: Config) {
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
            }

//            val filename = "LP/VCG/Exclude" + bidIndex.toString()
//            val vcgConfig = Config(default.provider, default.requester, default.resource, default.bidderFile, filename, default.auction, default.resultFile, default.profitRate)
//            SingleSidedAuction.makeLpFile(vcgConfig, Object.MAX, exbidders, resource)
//            val vcgCplex = LpImporter(filename).getCplex()
//            vcgCplex.solve()
//            val payment = vcgCplex.objValue - (objValue - bidders[bidIndex[0]].bids[bidIndex[1]].getValue())
//            val profit = bidders[bidIndex[0]].bids[bidIndex[1]].getValue() - payment
//            bidResults.add(BidResult(arrayOf(bidIndex[0], bidIndex[1]), payment, profit))

        }
        // JsonWriter(default.resultFile).makeFile(ResultConverter.toJson(Result(objValue, bidResults)))
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