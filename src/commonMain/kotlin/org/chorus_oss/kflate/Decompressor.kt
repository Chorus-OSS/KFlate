package org.chorus_oss.kflate

import kotlinx.io.bytestring.ByteString

interface Decompressor {
    fun decompress(data: ByteArray): ByteArray

    fun decompress(data: ByteString): ByteString = ByteString(decompress(data.toByteArray()))
}