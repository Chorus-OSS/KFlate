package org.chorus_oss.kflate

import java.util.zip.GZIPInputStream

internal class JvmGzipDecompressor : Decompressor {
    override fun decompress(data: ByteArray): ByteArray {
        return GZIPInputStream(data.inputStream()).use {
            it.readAllBytes()
        }
    }
}

internal actual fun platformGzipDecompressor(): Decompressor = JvmGzipDecompressor()