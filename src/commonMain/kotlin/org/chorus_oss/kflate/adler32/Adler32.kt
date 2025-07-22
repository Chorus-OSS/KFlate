package org.chorus_oss.kflate.adler32

import kotlinx.io.bytestring.ByteString
import org.chorus_oss.kflate.utils.UInt4
import org.chorus_oss.kflate.utils.chunkExact

class Adler32 {
    var a: UShort = 1u
    var b: UShort = 0u

    constructor()

    constructor(sum: UInt) {
        this.a = sum.toUShort()
        this.b = (sum shr 16).toUShort()
    }

    fun checksum(): UInt = (b.toUInt() shl 16) or (a.toUInt())

    fun write(bytes: ByteString) {
        this.write(bytes.toByteArray())
    }

    fun write(bytes: ByteArray) {
        this.compute(bytes)
    }

    fun compute(bytes: ByteString) {
        this.compute(bytes.toByteArray())
    }

    fun compute(bytes: ByteArray) {
        var a: UInt = this.a.toUInt()
        var b: UInt = this.b.toUInt()
        val a4 = UInt4(0u, 0u, 0u, 0u)
        val b4 = UInt4(0u, 0u, 0u, 0u)

        val split = bytes.size - (bytes.size % 4)
        val (bytes, remainder) = Pair(bytes.sliceArray(0..<split), bytes.sliceArray(split..<bytes.size))

        val (chunkIter, remainderChunk) = bytes.chunkExact(CHUNK_SIZE)
        for (chunk in chunkIter) {
            for (bytes in chunk.chunkExact(4).first) {
                val value = UInt4(bytes)
                a4 += value
                b4 += a4
            }

            b += CHUNK_SIZE.toUInt() * a
            a4 %= MOD
            b4 %= MOD
            b %= MOD
        }

        for (bytes in remainderChunk.chunkExact(4).first) {
            val value = UInt4(bytes)
            a4 += value
            b4 += a4
        }

        b += remainderChunk.size.toUInt() * a
        a4 %= MOD
        b4 %= MOD
        b %= MOD

        b4 *= 4u
        b4.b += MOD - a4.b
        b4.c += (MOD - a4.c) * 2u
        b4.d += (MOD - a4.d) * 3u
        for (av in a4) {
            a += av
        }
        for (bv in b4) {
            b += bv
        }

        for (byte in remainder) {
            a += byte.toUByte().toUInt()
            b += a
        }

        this.a = (a % MOD).toUShort()
        this.b = (b % MOD).toUShort()
    }

    companion object {
        const val MOD: UInt = 65521u
        const val CHUNK_SIZE: Int = 5552 * 4

        fun checksum(bytes: ByteString): UInt {
            return checksum(bytes.toByteArray())
        }

        fun checksum(bytes: ByteArray): UInt {
            val adler = Adler32()
            adler.write(bytes)
            return adler.checksum()
        }
    }
}