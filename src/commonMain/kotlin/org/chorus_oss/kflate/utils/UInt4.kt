package org.chorus_oss.kflate.utils

data class UInt4(
    var a: UInt,
    var b: UInt,
    var c: UInt,
    var d: UInt,
): Iterable<UInt> {
    constructor(bytes: ByteArray): this(
        bytes[0].toUByte().toUInt(),
        bytes[1].toUByte().toUInt(),
        bytes[2].toUByte().toUInt(),
        bytes[3].toUByte().toUInt(),
    )

    operator fun plusAssign(other: UInt4) {
        this.a += other.a
        this.b += other.b
        this.c += other.c
        this.d += other.d
    }

    operator fun remAssign(quotient: UInt) {
        this.a %= quotient
        this.b %= quotient
        this.c %= quotient
        this.d %= quotient
    }

    operator fun timesAssign(rhs: UInt) {
        this.a *= rhs
        this.b *= rhs
        this.c *= rhs
        this.d *= rhs
    }

    override fun iterator(): Iterator<UInt> {
        return listOf(this.a, this.b, this.c, this.d).iterator()
    }
}