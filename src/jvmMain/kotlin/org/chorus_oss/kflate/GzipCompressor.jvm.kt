package org.chorus_oss.kflate

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

internal class JvmGzipCompressor: Compressor {
    override fun compress(data: ByteArray): ByteArray {
        val out = ByteArrayOutputStream(4096)
        GZIPOutputStream(out).use {
            it.write(data)
            it.finish()
        }
        return out.toByteArray()
    }
}

internal actual fun platformGzipCompressor(): Compressor = JvmGzipCompressor()