package org.chorus_oss.kflate

interface Deflater {
    fun compress(data: ByteArray): ByteArray
}

expect fun Deflater(level: Int): Deflater