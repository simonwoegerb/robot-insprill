package net.insprill.robotinsprill.ocr

import net.insprill.robotinsprill.exception.TessInitFailureException
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.leptonica.PIX
import org.bytedeco.leptonica.global.leptonica.pixDestroy
import org.bytedeco.leptonica.global.leptonica.pixReadMem
import org.bytedeco.tesseract.TessBaseAPI

class Tesseract(private val bytes: ByteArray) {

    companion object {
        val enabled: Boolean
        val exception: Throwable?

        init {
            val res = kotlin.runCatching { TessBaseAPI().End() }
            enabled = res.isSuccess
            exception = res.exceptionOrNull()
        }
    }

    fun scan(): Result<String> {
        if (!enabled) return Result.failure(exception!!)
        var api: TessBaseAPI? = null
        var outText: BytePointer? = null
        var pixImage: PIX? = null
        try {
            api = TessBaseAPI()
            val initCode = api.Init("ocr", "eng")
            if (initCode != 0) {
                return Result.failure(TessInitFailureException())
            }
            pixImage = pixReadMem(bytes, bytes.size.toLong())
            api.SetImage(pixImage)
            outText = api.GetUTF8Text()
            return Result.success(outText.getString(Charsets.UTF_8))
        } finally {
            api?.End()
            outText?.deallocate()
            pixImage?.let { pixDestroy(it) }
        }
    }
}
