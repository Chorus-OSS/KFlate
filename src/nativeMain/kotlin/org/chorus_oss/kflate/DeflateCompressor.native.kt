package org.chorus_oss.kflate

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.IOException
import kotlinx.io.readByteArray
import platform.zlib.*

@OptIn(ExperimentalForeignApi::class)
internal class NativeDeflateCompressor(var level: Int) : Compressor {
    init {
        require(level in 0..9) { "level must be between 0 and 9" }
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
            var code = deflateInit2(
                zStream.ptr,
                level,
                Z_DEFLATED,
                -15,
                8,
                Z_DEFAULT_STRATEGY,
            )

            if (code != Z_OK) throw IOException("deflate compression failed, code: $code")

            val inBytesPtr = inBytes.pin()
            val outBytesPtr = outBytes.pin()
            try {
                zStream.next_in = inBytesPtr.addressOf(0)
                zStream.avail_in = inBytes.size.toUInt()
                zStream.next_out = outBytesPtr.addressOf(0)
                zStream.avail_out = outBytes.size.toUInt()

                var flush = Z_NO_FLUSH
                do {
                    if (zStream.avail_in.toInt() == 0) flush = Z_FINISH

                    code = deflate(zStream.ptr, flush)

                    if (code != Z_OK && code != Z_STREAM_END) throw IOException("deflate compression failed, code: $code")

                    if (zStream.avail_out == 0u) {
                        buf.write(outBytes.toByteArray())
                        zStream.next_out = outBytesPtr.addressOf(0)
                        zStream.avail_out = outBytes.size.toUInt()
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