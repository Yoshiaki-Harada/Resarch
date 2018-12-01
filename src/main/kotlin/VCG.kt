import Impoter.LpImporter
import ilog.concert.IloLPMatrix

public class VCG() {
    fun start(lpfile: String, bidders: ArrayList<Bidder>) {
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
        val x = ArrayList<ArrayList<Double>>()
        var index = 0
        for (i in 0 until bidders.size) {
            val tmpList = ArrayList<Double>()
            for (j in 0 until bidders[i].bids.size) {
                tmpList.add(xCplex[index])
                index += 1
            }
            x.add(tmpList)
        }

        //勝者となった入札を読み込む
        //勝者となった入札を除いた入札を作成する
        //定式化を行う
        //解く
        //最適解を求める
        //差額が支払い価格となる
        //繰り返す

        cplex.end()
    }
}