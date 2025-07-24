package org.chorus_oss.kflate

interface Compressor {
    fun compress(data: ByteArray): ByteArray
}