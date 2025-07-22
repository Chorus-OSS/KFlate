package org.chorus_oss.kflate.lz77

object LZ77Common {
    const val WINDOW_SIZE = 1 shl 15
    const val INIT: Short = (-WINDOW_SIZE).toShort()

    fun rebase(table: ShortArray) {
        for (i in table.indices) {
            val pos = table[i]
            table[i] = if (pos >= 0) (pos + INIT).toShort() else (INIT)
        }
    }

    fun extend(
        data: ByteArray,
        pos: Int,
        match: Int,
        initLen: Int,
        maxLen: Int
    ): Int {
        val size = data.size

        var len = initLen
        while (
            len < maxLen &&
            pos + len < size &&
            data[pos + len] == data[match + len]
        ) {
            len++
        }
        return len
    }

    fun hash(seq: UInt, numBits: Int): Int {
        return ((seq * 0x1E35A7BDu) shr (32 - numBits)).toInt()
    }

    fun readU24(data: ByteArray, pos: Int): UInt {
        return (
                    (data[pos].toUInt() shl 16)
                    or (data[pos + 1].toUInt() shl 8)
                    or (data[pos + 2].toUInt())
                )
    }

    fun readU32(data: ByteArray, pos: Int): UInt {
        return (
                (data[pos].toUInt() shl 24)
                        or (data[pos + 1].toUInt() shl 16)
                        or (data[pos + 2].toUInt() shl 8)
                        or (data[pos + 3].toUInt())
                )
    }
}