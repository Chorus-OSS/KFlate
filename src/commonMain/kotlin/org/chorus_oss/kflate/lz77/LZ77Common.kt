package org.chorus_oss.kflate.lz77

object LZ77Common {
    const val WINDOW_SIZE = 1 shl 15
    const val WINDOW_INIT: Short = (-WINDOW_SIZE).toShort()

    fun init(table: ShortArray) {
        table.fill(WINDOW_INIT)
    }

    fun rebase(table: ShortArray) {
        for (i in table.indices) {
            val pos = table[i]
            table[i] = if (pos >= 0) (pos + WINDOW_INIT).toShort() else (WINDOW_INIT)
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

    fun hash(seq: UInt, numBits: Byte): UInt {
        return (seq * 0x1E35A7BDu) shr (32 - numBits)
    }
}