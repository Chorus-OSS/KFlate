package org.chorus_oss.kflate

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.IOException
import kotlinx.io.readByteArray
import platform.zlib.*

@OptIn(ExperimentalForeignApi::class)
internal class NativeDeflateDecompressor : Decompressor {
    override fun decompress(data: ByteArray): ByteArray {
        if (data.isEmpty()) return byteArrayOf()

        val buf = Buffer()
        memScoped {
            val inBytes = data.toUByteArray()
            val outBytes = UByteArray(16384)
            val zStream = alloc<z_stream>()
            zStream.zalloc = null
            zStream.zfree = null
            zStream.opaque = null
            var code = inflateInit(
                zStream.ptr,
            )

            if (code != Z_OK) throw IOException("deflate decompression failed, code: $code")

            val inBytesPtr = inBytes.pin()
            val outBytesPtr = outBytes.pin()
            try {
                zStream.next_in = inBytesPtr.addressOf(0)
                zStream.avail_in = inBytes.size.toUInt()
                zStream.next_out = outBytesPtr.addressOf(0)
                zStream.avail_out = outBytes.size.toUInt()
                do {
                    code = inflate(zStream.ptr, Z_FINISH)

                    if (code != Z_OK && code != Z_STREAM_END) throw IOException("deflate decompression failed, code: $code")

                    if (zStream.avail_out == 0u) {
                        buf.write(outBytes.toByteArray())
                    }
                } while (code == Z_OK)
                buf.write(outBytes.copyOfRange(0, outBytes.size - zStream.avail_out.toInt()).toByteArray())
            } finally {
                inBytesPtr.unpin()
                outBytesPtr.unpin()
                inflateEnd(zStream.ptr)
            }
        }

        return buf.readByteArray()
    }
}

internal actual fun platformDeflateDecompressor(): Decompressor = NativeDeflateDecompressor()