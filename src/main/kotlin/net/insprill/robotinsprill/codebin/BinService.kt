package net.insprill.robotinsprill.codebin

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.jsonBody
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.insprill.robotinsprill.configuration.BotConfig
import net.insprill.robotinsprill.extension.awaitObjectKResult
import net.insprill.robotinsprill.extension.awaitStringKResult
import net.insprill.robotinsprill.extension.urlEncoded

@Suppress("unused")
enum class BinService(private val downloadUrl: String) {
    HASTEBIN_LEGACY("https://%s/raw/%s") {
        override suspend fun uploadBinReq(domain: String, data: String): Request {
            return Fuel.post("https://$domain/documents").body(data)
        }
    },
    LUCKO_PASTE("https://%s/data/%s") {
        override suspend fun uploadBinReq(domain: String, data: String): Request {
            return Fuel.post("https://$domain/data/post").body(data)
        }
    },
    PASTEBIN("https://%s/raw/%s") {
        override suspend fun uploadBinReq(domain: String, data: String): Request {
            // https://pastebin.com/doc_api
            val encodedKey = System.getenv("PASTEBIN_API_KEY").urlEncoded()
            val encodedBody = data.urlEncoded()
            val body = "api_dev_key=$encodedKey&api_option=paste&api_paste_private=1&api_paste_code=$encodedBody"
            return Fuel.post("https://$domain/api/api_post.php").body(body)
                .header("content-type", "application/x-www-form-urlencoded; charset=utf-8")
        }

        override suspend fun uploadBin(domain: String, data: String): Result<String> {
            uploadBinReq(domain, data).awaitStringKResult()
                .fold({ res -> return Result.success(res) },
                    { err -> return Result.failure(err) })
        }
    },
    SOURCE_BIN("https://cdn.%s/bins/%s") {
        override suspend fun uploadBinReq(domain: String, data: String): Request {
            val reqBody = SourceBinRequest(listOf(SourceBinRequest.File("", data)))
            return Fuel.post("https://$domain/api/bins").jsonBody(Json.encodeToString(reqBody))
        }
    },
    ;

    protected abstract suspend fun uploadBinReq(domain: String, data: String): Request

    protected open suspend fun uploadBin(domain: String, data: String): Result<String> {
        uploadBinReq(domain, data).awaitObjectKResult<GenericBinResponse>()
            .fold({ res -> return Result.success("https://$domain/${res.key}") },
                { err -> return Result.failure(err) })
    }

    suspend fun uploadBin(config: BotConfig, data: String): Result<String> {
        val domain = config.codebin.services[this]!!.first()
        return uploadBin(domain, data)
    }

    suspend fun downloadBin(domain: String, key: String): Result<String> {
        Fuel.get(this.downloadUrl.format(domain, key)).awaitStringKResult()
            .fold({ res -> return Result.success(res) },
                { err -> return Result.failure(err) })
    }

    @Serializable
    data class GenericBinResponse(val key: String)

    @Serializable
    data class SourceBinRequest(val files: Iterable<File>) {
        @Serializable
        data class File(val name: String, val content: String)
    }

}
