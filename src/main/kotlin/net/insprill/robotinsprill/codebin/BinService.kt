package net.insprill.robotinsprill.codebin

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.URLEncoder
import net.insprill.robotinsprill.configuration.BotConfig

@Suppress("unused")
enum class BinService(private val downloadUrl: String) {
    HASTEBIN_LEGACY("https://%s/raw/%s") {
        override suspend fun uploadBinReq(domain: String, data: String): Request {
            return Fuel.post("https://$domain/documents").body(data, Charsets.UTF_8)
        }
    },
    LUCKO_PASTE("https://%s/data/%s") {
        override suspend fun uploadBinReq(domain: String, data: String): Request {
            return Fuel.post("https://$domain/data/post").body(data, Charsets.UTF_8)
        }
    },
    PASTEBIN("https://%s/raw/%s") {
        override suspend fun uploadBinReq(domain: String, data: String): Request {
            // https://pastebin.com/doc_api
            val encodedKey = URLEncoder.encode(System.getenv("PASTEBIN_KEY"), "UTF-8")
            val encodedBody = URLEncoder.encode(data, "UTF-8")
            val body = "api_dev_key=$encodedKey&api_option=paste&api_paste_private=1&api_paste_code=$encodedBody"
            return Fuel.post("https://$domain/api/api_post.php").body(body, Charsets.UTF_8)
                .header("content-type", "application/x-www-form-urlencoded; charset=utf-8")
        }

        override suspend fun uploadBin(domain: String, data: String): Result<String> {
            uploadBinReq(domain, data).awaitStringResult(Charsets.UTF_8)
                .fold({ res -> return Result.success(res) },
                    { err -> return Result.failure(err) })
        }
    },
    SOURCE_BIN("https://cdn.%s/bins/%s") {
        override suspend fun uploadBinReq(domain: String, data: String): Request {
            val root = JsonObject()
            val files = JsonArray()
            val file = JsonObject()
            file.addProperty("name", "")
            file.addProperty("content", data)
            files.add(file)
            root.add("files", files)
            return Fuel.post("https://$domain/api/bins").jsonBody(root.toString(), Charsets.UTF_8)
        }
    },
    ;

    protected abstract suspend fun uploadBinReq(domain: String, data: String): Request

    protected open suspend fun uploadBin(domain: String, data: String): Result<String> {
        uploadBinReq(domain, data).awaitStringResult(Charsets.UTF_8)
            .fold({ res -> return Result.success("https://$domain/${JsonParser.parseString(res).asJsonObject.get("key").asString}") },
                { err -> return Result.failure(err) })
    }

    suspend fun uploadBin(config: BotConfig, data: String): Result<String> {
        val domain = config.codebin.services[this]!!.first()
        return uploadBin(domain, data)
    }

    suspend fun downloadBin(domain: String, key: String): Result<String> {
        Fuel.get(this.downloadUrl.format(domain, key)).awaitStringResult(Charsets.UTF_8)
            .fold({ res -> return Result.success(res) },
                { err -> return Result.failure(err) })
    }

}
