package org.chorus_oss.kflate.lz77

import kotlinx.io.bytestring.ByteString

class LZ77(
    val maxChain: Int,
) {
    fun compress(data: ByteArray): List<LZ77Token> {
        val tokens = mutableListOf<LZ77Token>()

        val head = IntArray(HASH_SIZE) { -1 }
        val prev = IntArray(data.size) { -1 }

        fun hash(pos: Int): Int {
            return if (pos + 2 >= data.size) {
                0
            } else {
                (
                    (data[pos].toInt() shl 8)
                    xor data[pos + 1].toInt()
                    xor data[pos + 2].toInt()
                ) and HASH_MASK
            }
        }

        var pos = 0
        while (pos < data.size) {
            val hash = hash(pos)

            val prevMatch = head[hash]

            head[hash] = pos
            prev[pos] = prevMatch

            var length = 0
            var offset = 0
            var chains = 0
            var candidate = prevMatch

            while (candidate != -1 && chains < maxChain && pos - candidate <= HASH_SIZE) {
                var len = 0
                while (len < MAX_MATCH_SIZE && pos + len < data.size && data[candidate + len] == data[pos + len]) {
                    len++
                }
                if (len > length) {
                    length = len
                    offset = pos - candidate
                    if (len >= MAX_MATCH_SIZE) break
                }
                candidate = prev[candidate]
                chains++
            }

            if (length >= MIN_MATCH_SIZE) {
                tokens += LZ77Token.Match(
                    offset = offset.toUShort(),
                    length = length.toUShort(),
                )
                repeat(length) {
                    if (pos + it + 2 < data.size) {
                        val h2 = hash(pos + it)
                        prev[pos + it] = head[h2]
                        head[h2] = pos + it
                    }
                }
                pos += length
            } else {
                tokens += LZ77Token.Literal(data[pos])
                pos++
            }
        }

        tokens += LZ77Token.Literal(Byte.MAX_VALUE)
        return tokens
    }

    fun compress(data: ByteString): List<LZ77Token> {
        return compress(data.toByteArray())
    }

    companion object {
        const val HASH_SIZE = 32_767
        const val HASH_MASK = HASH_SIZE - 1

        const val MIN_MATCH_SIZE = 3
        const val MAX_MATCH_SIZE = 258
    }
}