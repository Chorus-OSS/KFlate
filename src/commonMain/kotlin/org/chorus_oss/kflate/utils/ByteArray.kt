package org.chorus_oss.kflate.utils

fun ByteArray.chunk(size: Int): List<ByteArray> {
    val chunks = mutableListOf<ByteArray>()
    var remainder = this
    while (remainder.size > size) {
        chunks += remainder.sliceArray(0..<size)
        remainder = remainder.sliceArray(size..<remainder.size)
    }
    if (remainder.isNotEmpty()) chunks += remainder
    return chunks
}

fun ByteArray.chunkExact(size: Int): Pair<List<ByteArray>, ByteArray> {
    val rem = this.size % size
    val len = this.size - rem

    val chunks = this.sliceArray(0..<len).chunk(size)
    val remainder = this.sliceArray(len..<this.size)

    return Pair(chunks, remainder)
}