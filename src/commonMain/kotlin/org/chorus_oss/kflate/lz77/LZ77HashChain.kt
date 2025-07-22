package org.chorus_oss.kflate.lz77

import kotlinx.io.bytestring.ByteString

class LZ77HashChain(
    val maxMatchSize: Int,
    val minMatchSize: Int,
    val niceMatchSize: Int,
    val maxChain: Int,
) {
    fun compress(data: ByteArray): List<LZ77Token> {
        val tokens = mutableListOf<LZ77Token>()

        val hash3 = ShortArray(HASH3_SIZE) { LZ77Common.INIT }
        val hash4 = ShortArray(HASH4_SIZE) { LZ77Common.INIT }
        val next = ShortArray(LZ77Common.WINDOW_SIZE) { LZ77Common.INIT }

        fun insert(pos: Int) {
            if (pos + 3 >= data.size) return

            val h3 = LZ77Common.hash(LZ77Common.readU24(data, pos), HASH3_ORDER)
            val h4 = LZ77Common.hash(LZ77Common.readU32(data, pos), HASH4_ORDER)

            next[pos] = hash4[h4]
            hash3[h3] = pos.toShort()
            hash4[h4] = pos.toShort()
        }

        fun find(pos: Int): LZ77Token.Match? {
            if (pos + 3 >= data.size) return null

            val h3 = LZ77Common.hash(LZ77Common.readU24(data, pos), HASH3_ORDER)
            val h4 = LZ77Common.hash(LZ77Common.readU32(data, pos), HASH4_ORDER)

            val match3 = hash3[h3].toInt()
            val match4 = hash4[h4].toInt()

            var length = 0
            var offset = 0

            if (match3 >= 0 && pos - match3 <= LZ77Common.WINDOW_SIZE) {
                if (LZ77Common.extend(data, pos, match3, 3, maxMatchSize) == 3) {
                    length = 3
                    offset = pos - match3
                }
            }

            var chain = 0
            var match = match4
            while (match >= 0 && chain++ < maxChain && pos - match <= LZ77Common.WINDOW_SIZE) {
                val len = LZ77Common.extend(data, pos, match, 4, maxMatchSize)
                if (len > length) {
                    length = len
                    offset = pos - match
                    if (len >= niceMatchSize) break
                }
                match = next[match].toInt()
            }

            return if (length >= minMatchSize) LZ77Token.Match(offset.toUShort(), length.toUShort()) else null
        }


        var pos = 0
        while (pos < data.size - 4) {
            if (pos == LZ77Common.WINDOW_SIZE) {
                LZ77Common.rebase(hash3)
                LZ77Common.rebase(hash4)
                LZ77Common.rebase(next)
            }

            val match = find(pos)
            if (match != null) {
                tokens += match
                repeat(match.length.toInt()) { insert(pos + it) }
                pos += match.length.toInt()
            } else {
                tokens += LZ77Token.Literal(data[pos])
                insert(pos)
                pos++
            }
        }

        return tokens
    }

    fun compress(data: ByteString): List<LZ77Token> {
        return compress(data.toByteArray())
    }

    companion object {
        const val HASH3_ORDER = 15
        const val HASH4_ORDER = 16

        const val HASH3_SIZE = 1 shl HASH3_ORDER
        const val HASH4_SIZE = 1 shl HASH4_ORDER

        const val MIN_MATCH_SIZE = 3
        const val MAX_MATCH_SIZE = 258
    }
}