package exercise

import cplex.Solver
import cplex.lpformat.Constrait
import cplex.lpformat.Object
import cplex.lpformat.VarType
import ilog.concert.IloLPMatrix
import impoter.LpImporter
import writer.LpWriter

data class Bid(val index: Int, val bundle: DoubleArray, val value: Double)
data class Resource(val volume: DoubleArray, val value: Double)
data class Buyer(val index: Int, val bids: List<Bid>)
data class Seller(val index: Int, val resource: Resource)

fun makeOriginalProblem(fileName: String, buyers: List<Buyer>, sellers: List<Seller>) {
    val lp = LpWriter(fileName)

    // 目的関数
    lp.obj(Object.MAX)
    buyers.forEach { bidder ->
        bidder.bids.forEach { bid ->
            lp.term(bid.value, "x", "${bidder.index}${bid.index}")
        }
    }

    sellers.forEach { seller ->
        lp.minus(seller.resource.value, "y", "${seller.index}")
    }
    lp.newline()

    // 制約条件
    lp.subto()

    // 財Aについて
    buyers.forEach { bidder ->
        bidder.bids.forEach { bid ->
            lp.term(bid.bundle[0], "x", "${bidder.index}${bid.index}")
        }
    }

    sellers.forEachIndexed { j, seller ->
        lp.minus(seller.resource.volume[0], "y", "${seller.index}")
    }
    lp.constrait(Constrait.EQ)
    lp.number(0.0)

    lp.newline()

    // 財Bについて
    buyers.forEach { bidder ->
        bidder.bids.forEach { bid ->
            lp.term(bid.bundle[1], "x", "${bidder.index}${bid.index}")
        }
    }

    sellers.forEach { seller ->
        lp.minus(seller.resource.volume[1], "y", "${seller.index}")
    }
    lp.constrait(Constrait.EQ)
    lp.number(0.0)
    lp.newline()

    // 単一バンドル指向
    buyers.forEach { buyer ->
        buyer.bids.forEach { bid ->
            lp.term("x", "${buyer.index}${bid.index}")
        }
        lp.constrait(Constrait.EQ)
        lp.number(1.0)
        lp.newline()
    }
    lp.newline()

    // 提供側の容量制約
    sellers.forEach { seller ->
        lp.variable("y", "${seller.index}")
        lp.constrait(Constrait.LEQ)
        lp.number(seller.resource.volume[0] + seller.resource.volume[1])
        lp.newline()
    }
    lp.newline()

    // x \in {0, 1}
    lp.varType(VarType.BIN)
    buyers.forEachIndexed { i, bidder ->
        bidder.bids.forEachIndexed { b, bid ->
            lp.variable("x", "${bidder.index}${bid.index}")
        }
    }
    lp.newline()

    lp.varType(VarType.GEN)
    sellers.forEachIndexed { j, resource ->
        lp.variable("y", "$j")
    }
    lp.newline()
    lp.end()
}

fun makePaddedProblem(fileName: String, buyers: List<Buyer>, sellers: List<Seller>) {
    val lp = LpWriter(fileName)
    val q = listOf(2.0, 1.0)

    // 目的関数
    lp.obj(Object.MAX)
    buyers.forEach { bidder ->
        bidder.bids.forEach { bid ->
            lp.term(bid.value, "x", "${bidder.index}${bid.index}")
        }
    }
    sellers.forEach { seller ->
        lp.minus(seller.resource.value, "y", "${seller.index}")
    }
    lp.newline()

    // 制約条件
    lp.subto()

    // 財Aについて
    buyers.forEach { bidder ->
        bidder.bids.forEach { bid ->
            lp.term(bid.bundle[0], "x", "${bidder.index}${bid.index}")
        }
    }

    sellers.forEachIndexed { j, seller ->
        if (seller.resource.volume[0] > 0) {
            lp.minus(1.0, "y", "${seller.index}")
        }
    }


    lp.constrait(Constrait.EQ)
    lp.number(-q[0])

    lp.newline()

    // 財Bについて
    buyers.forEach { buyer ->
        buyer.bids.forEach { bid ->
            lp.term(bid.bundle[1], "x", "${buyer.index}${bid.index}")
        }
    }
    sellers.forEach { seller ->
        if (seller.resource.volume[1] > 0) {
            lp.minus(1.0, "y", "${seller.index}")
        }
    }
    lp.constrait(Constrait.EQ)
    lp.number(-q[1])

    lp.newline()

    // 単一バンドル指向
    buyers.forEach { buyer ->
        buyer.bids.forEach { bid ->
            lp.term("x", "${buyer.index}${bid.index}")
        }
        lp.constrait(Constrait.EQ)
        lp.number(1.0)
        lp.newline()
    }
    lp.newline()

    // 提供側の容量制約
    lp.bounds()
    sellers.forEach { seller ->
        lp.variable("y", "${seller.index}")
        lp.constrait(Constrait.LEQ)
        lp.number(seller.resource.volume[0] + seller.resource.volume[1])
        lp.newline()
    }
    lp.newline()

    // x \in {0, 1}
    lp.varType(VarType.BIN)
    buyers.forEachIndexed { i, bidder ->
        bidder.bids.forEachIndexed { b, bid ->
            lp.variable("x", "${bidder.index}${bid.index}")
        }
    }
    lp.newline()

    lp.varType(VarType.GEN)
    sellers.forEachIndexed { j, resource ->
        lp.variable("y", "$j")
    }
    lp.newline()
    lp.end()

}

fun makeWinSetsProblem(fileName: String, winBuyers: List<Buyer>, sellers: List<Seller>) {
    val lp = LpWriter(fileName)

    // 目的関数
    lp.obj(Object.MAX)
    winBuyers.forEachIndexed { i, buyer ->
        lp.term(buyer.bids.first().value, "x", "${buyer.index}")
    }

    sellers.forEachIndexed { j, seller ->
        lp.minus(seller.resource.value, "y", "${seller.index}")
    }
    lp.newline()

    // 制約条件
    lp.subto()

    // 財Aについて
    winBuyers.forEachIndexed { i, buyer ->
        lp.term(buyer.bids.first().bundle[0], "x", "${buyer.index}")
    }

    sellers.forEachIndexed { j, seller ->
        if (seller.resource.volume[0] > 0) {
            lp.minus(1.0, "y", "${seller.index}")
        }
    }
    lp.constrait(Constrait.EQ)
    lp.number(0.0)

    lp.newline()

    // 財Bについて
    winBuyers.forEachIndexed { i, buyer ->
        lp.term(buyer.bids.first().bundle[1], "x", "${buyer.index}")
    }

    sellers.forEachIndexed { j, seller ->
        if (seller.resource.volume[1] > 0) {
            lp.minus(1.0, "y", "${seller.index}")
        }
    }
    lp.constrait(Constrait.EQ)
    lp.number(0.0)
    lp.newline()


    // 提供側の容量制約
    sellers.forEachIndexed { j, seller ->
        lp.variable("y", "$j")
        lp.constrait(Constrait.LEQ)
        lp.number(seller.resource.volume[0] + seller.resource.volume[1])
        lp.newline()
    }
    lp.newline()

    // x \in {0, 1}
    lp.varType(VarType.BIN)
    winBuyers.forEachIndexed { i, bidder ->
        lp.variable("x", "${bidder.index}")
    }
    lp.newline()

    lp.varType(VarType.GEN)
    sellers.forEachIndexed { j, seller ->
        lp.variable("y", "${seller.index}")
    }
    lp.newline()
    lp.end()
}

fun getBuyerPay(targetBuyer: Int, targetBid: Int, paddedObjValue: Double, buyers: List<Buyer>, sellers: List<Seller>): Double {
    makePaddedProblem("vcg", buyers.filter { it.index != targetBuyer }, sellers)

    val result = Solver(LpImporter("vcg").getCplex()).solve()
    val objValue = result.objValue
    println("objValue $objValue")
    println("paddedObjValue $paddedObjValue")
    return buyers[targetBuyer].bids[targetBid].value - paddedObjValue + objValue
}

fun getSellerLessPay(targetSeller: Int, winSetsObjValue: Double, buyers: List<Buyer>, sellers: List<Seller>): Double {

    println("buyers: $buyers")
    makeWinSetsProblem("less", buyers, sellers.map {
        if (it.index == targetSeller) {
            Seller(0, Resource(doubleArrayOf(2.0, 0.0), 3.0))
        } else {
            it
        }
    })

    val result1 = Solver(LpImporter("less").getCplex()).solve()
    val objValue1 = result1.objValue

    println("winSetsObjValue = $winSetsObjValue")
    println("V(I,J\\{j}) = $objValue1")

    makeWinSetsProblem("sell-vcg", buyers, sellers.filter { it.index != targetSeller })
    val result2 = Solver(LpImporter("sell-vcg").getCplex()).solve()
    val objValue2 = result2.objValue

    return objValue1 - objValue2
}

fun getSellerVCGPay(targetSeller: Int, provideUnit: Double, winSetsObjValue: Double, buyers: List<Buyer>, sellers: List<Seller>): Double {
    // V(I,J\{j})
    makeWinSetsProblem("sell-vcg", buyers, sellers.filter { it.index != targetSeller })
    val result = Solver(LpImporter("sell-vcg").getCplex()).solve()
    val objValue = result.objValue

    println("gj*yj = ${sellers[targetSeller].resource.value * provideUnit}")
    println("winSetsObjValue = $winSetsObjValue")
    println("V(I,J\\{j}) = $objValue")

    return sellers[targetSeller].resource.value * provideUnit + winSetsObjValue - objValue
}


fun main() {

    // 元の問題
    val b0 = Bid(0, doubleArrayOf(2.0, 0.0), 9.0)
    val b1 = Bid(1, doubleArrayOf(0.0, 1.0), 3.0)
    val buyers = listOf(Buyer(0, listOf(b0, b1)))

    val s0 = Resource(doubleArrayOf(2.0, 0.0), 1.0)
    val s1 = Resource(doubleArrayOf(1.0, 0.0), 2.0)
    val s2 = Resource(doubleArrayOf(1.0, 0.0), 4.0)
    val s3 = Resource(doubleArrayOf(0.0, 1.0), 1.0)
    val s4 = Resource(doubleArrayOf(0.0, 1.0), 1.0)
    val sellers = listOf(Seller(0, s0), Seller(1, s1), Seller(2, s2), Seller(3, s3), Seller(4, s4))


    // Padded
    makePaddedProblem("padded", buyers, sellers)
    val paddedResult = Solver(LpImporter("padded").getCplex()).solve()
    val paddedObjValue = paddedResult.objValue
    val paddedSolution = paddedResult.getValues(paddedResult.LPMatrixIterator().next() as IloLPMatrix)

    println("paddedObjValue = $paddedObjValue")

    // 買い手の支払い価格を求める
    // 二次元配列に戻す
    var count = 0
    val x = mutableListOf<List<Double>>()
    buyers.forEach { buyer ->
        x.add(paddedSolution.copyOfRange(count, buyer.bids.size).toList())
        count += buyer.bids.size
    }

    // VCG
    val buyerPays = x.mapIndexed { i, buyer ->
        buyer.mapIndexed { b, value ->
            if (value in 0.9..1.1) {
                println("勝者となった入札: $b")
                getBuyerPay(i, b, paddedObjValue, buyers, sellers)
            } else {
                0.0
            }
        }.sum()
    }
    println("buyersPay: $buyerPays")

    //売手0のVCGを求める
    val result = Solver(LpImporter("seller0").getCplex()).solve()
    val objValue = result.objValue
    println("objValue: ${objValue}")
    println("paddedObjValue :$paddedObjValue")
    println("price: ${2.0 - paddedObjValue + objValue}")

    //  勝者の集合を集める
    val winBuyers = mutableListOf<Buyer>()
    x.forEachIndexed { i, buyer ->
        buyer.forEachIndexed { j, value ->
            if (value in 0.9..1.1)
                winBuyers.add(Buyer(buyers[i].index, listOf(buyers[i].bids[j])))
        }
    }

    println("winBuyers: $winBuyers")

    makeWinSetsProblem("winSets", winBuyers, sellers)
    val winSetsResult = Solver(LpImporter("winSets").getCplex()).solve()
    val winSetsObjValue = winSetsResult.objValue
    val winSetsResultSolution = winSetsResult.getValues(winSetsResult.LPMatrixIterator().next() as IloLPMatrix)

    val sellerSolutions = winSetsResultSolution.copyOfRange(winBuyers.flatMap { it.bids.map { it } }.size, winSetsResultSolution.lastIndex + 1).toList()

    println("size = ${sellerSolutions.size}")

    // V(I^{~},J|gj=pj(I,J,Q))

    val sellerPays = sellers.map {
        if (sellerSolutions[it.index] > 0.9) {
            getSellerVCGPay(it.index, sellerSolutions[it.index], winSetsObjValue, winBuyers, sellers)
        } else {
            0.0
        }
    }
    println("sellerVCGPays = $sellerPays")

    val paddedSellerSoulution = paddedSolution.copyOfRange(buyers.flatMap { it.bids.map { it } }.size, paddedSolution.lastIndex + 1).toList()

    // J|gj=pj(I,J,Q)
    makeWinSetsProblem("lp1", winBuyers, sellers)
    val resultLp1 = Solver(LpImporter("lp1").getCplex()).solve()
    val lp1ObjValue = resultLp1.objValue


    val lessSellerPay = sellers.map {
        if (sellerSolutions[it.index] > 0.9) {
            getSellerLessPay(it.index, sellerSolutions[it.index], winBuyers, sellers)
        } else {
            0.0
        }
    }

    println("lessPay = ${lessSellerPay}")

}