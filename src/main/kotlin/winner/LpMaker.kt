package winner

import config.Config
import model.Bidder
import model.Option

interface LpMaker {
    fun makeLpFile(config: Config, obj: cplex.lpformat.Object, bidders: List<Bidder>, vararg option: Option)
}