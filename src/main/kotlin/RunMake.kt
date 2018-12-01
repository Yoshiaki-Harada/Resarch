fun main(args: Array<String>){
    val bidders = ArrayList<Bidder>()
    val i = 3
    for (j in  0 until 3){
        val bidder = Bidder().fromJson(JsonImporter("Bid/Bidder"+j.toString()).getString())
        bidders.add(bidder)
    }
    LPMaker("LP/example2",bidders).makeFile()
}
