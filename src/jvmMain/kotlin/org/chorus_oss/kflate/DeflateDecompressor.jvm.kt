package org.chorus_oss.kflate

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import java.util.zip.Inflater

internal class JvmDeflateDecompressor : Decompressor {
    override fun decompress(data: ByteArray): ByteArray {
        if (data.isEmpty()) return byteArrayOf()

        val inflater = Inflater(true)
        val buf = Buffer()
        try {
            inflater.setInput(data)

            val out = ByteArray(4096)

            while (!inflater.finished()) {
                val len = inflater.inflate(out)
                if (len > 0) buf.write(out, 0, len)
            }
        } finally {
            inflater.end()
        }

        return buf.readByteArray()
    }
}

internal actual fun platformDeflateDecompressor(): Decompressor = JvmDeflateDecompressor()