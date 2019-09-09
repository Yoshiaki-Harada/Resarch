package winner

import config.Config
import model.Bidder
import model.Option

interface LpMaker {
    /**
     * lpファイルはlpDir/lpfileに保存される
     */
    fun makeLpFile()
}