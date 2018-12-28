package lpfile

import config.Config
import converter.ConfigConverter
import model.Bidder
import model.Option

interface LpMaker {

    fun makeLpFile(config: Config, obj: lpfile.lpformat.Object, bidders: List<Bidder>, vararg option: Option)

}