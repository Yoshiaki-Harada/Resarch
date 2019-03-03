package dataset

import config.Config

interface DataMaker<T> {
    fun run(config: Config): T
}
