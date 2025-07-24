package org.chorus_oss.kflate

import kotlinx.io.Buffer
import kotlinx.io.readByteArray

class JvmDeflater(var level: Int) : Deflater {
    init {
        require(level in 0..9) { "Deflater level must be between 0 and 9" }
    }

    override fun compress(data: ByteArray): ByteArray {
        if (data.isEmpty()) return byteArrayOf()

        val deflater = java.util.zip.Deflater(level, true)
        val buf = Buffer()
        try {
            deflater.setInput(data)
            deflater.finish()

            val out = ByteArray(4096)

            while (!deflater.finished()) {
                val len = deflater.deflate(out)
                if (len > 0) buf.write(out, 0, len)
            }
        } finally {
            deflater.end()
        }

        return buf.readByteArray()
    }
}