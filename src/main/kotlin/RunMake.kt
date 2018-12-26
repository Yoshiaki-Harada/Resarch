import Impoter.JsonImporter
import config.Config
import lpformat.Object
import model.Bidder

fun main(args: Array<String>) {
    val config = Config.fromJson("config")
    val bidders: MutableList<Bidder> = mutableListOf()
    for (j in 0 until config.bidder) {
        val bidder = Bidder().fromJson(JsonImporter(config.bidderFile + j.toString()).getString())
        bidders.add(bidder)
    }
    LPMaker(config.lpFile, Object.MAX, bidders, arrayOf(1.0, 1.0)).makeFile()
}
