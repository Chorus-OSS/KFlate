package org.chorus_oss.kflate

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pin
import kotlinx.cinterop.ptr
import kotlinx.io.Buffer
import kotlinx.io.IOException
import kotlinx.io.readByteArray
import platform.zlib.Z_FINISH
import platform.zlib.Z_OK
import platform.zlib.Z_STREAM_END
import platform.zlib.deflate
import platform.zlib.deflateEnd
import platform.zlib.deflateInit
import platform.zlib.z_stream

@OptIn(ExperimentalForeignApi::class)
internal class NativeDeflateCompressor(var level: Int) : Compressor {
    init {
        require(level in 0..9) { "Deflater level must be between 0 and 9" }
    }

    override fun compress(data: ByteArray): ByteArray {
        if (data.isEmpty()) return byteArrayOf()

        val buf = Buffer()
        memScoped {
            val inBytes = data.toUByteArray()
            val outBytes = UByteArray(16384)
            val zStream = alloc<z_stream>()
            zStream.zalloc = null
            zStream.zfree = null
            zStream.opaque = null
            var code = deflateInit(
                zStream.ptr,
                level,
            )

            if (code != Z_OK) throw IOException("deflate compression failed, code: $code")

            val inBytesPtr = inBytes.pin()
            val outBytesPtr = outBytes.pin()
            try {
                zStream.next_in = inBytesPtr.addressOf(0)
                zStream.avail_in = inBytes.size.toUInt()
                zStream.next_out = outBytesPtr.addressOf(0)
                zStream.avail_out = outBytes.size.toUInt()
                do {
                    code = deflate(zStream.ptr, Z_FINISH)

                    if (code != Z_OK && code != Z_STREAM_END) throw IOException("deflate compression failed, code: $code")

                    if (zStream.avail_out == 0u) {
                        buf.write(outBytes.toByteArray())
                    }
                } while (code == Z_OK)
                buf.write(outBytes.copyOfRange(0, outBytes.size - zStream.avail_out.toInt()).toByteArray())
            } finally {
                inBytesPtr.unpin()
                outBytesPtr.unpin()
                deflateEnd(zStream.ptr)
            }
        }

        return buf.readByteArray()
    }
}

internal actual fun platformDeflateCompressor(level: Int): Compressor = NativeDeflateCompressor(level)