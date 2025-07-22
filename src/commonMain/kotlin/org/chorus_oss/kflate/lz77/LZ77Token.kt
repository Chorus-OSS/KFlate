package org.chorus_oss.kflate.lz77

internal sealed interface LZ77Token {
    data class Literal(val value: Byte) : LZ77Token
    data class Match(val offset: UShort, val length: UShort) : LZ77Token
}
