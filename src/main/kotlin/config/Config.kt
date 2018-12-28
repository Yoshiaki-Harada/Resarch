package config

import Impoter.JsonImporter
import converter.ConfigConverter
import jdk.internal.loader.Resource

data class Config(val provider: Int,
                  val requester: Int,
                  val resource: Int,
                  val bidderFile: String,
                  val lpFile: String,
                  val auction: Int,
                  val resultFile: String,
                  val tmpFile: String) {
    companion object {
        fun fromJson(filePath: String): Config {
            return ConfigConverter.fromJson(JsonImporter(filePath).getString())
        }
    }
}