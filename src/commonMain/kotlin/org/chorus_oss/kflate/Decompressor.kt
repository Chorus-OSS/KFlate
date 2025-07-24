package org.chorus_oss.kflate

interface Decompressor {
    fun decompress(data: ByteArray): ByteArray
}