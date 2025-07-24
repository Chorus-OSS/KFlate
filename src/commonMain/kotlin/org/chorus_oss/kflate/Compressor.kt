package org.chorus_oss.kflate

import kotlinx.io.bytestring.ByteString

interface Compressor {
    fun compress(data: ByteArray): ByteArray

    fun compress(data: ByteString): ByteString = ByteString(compress(data.toByteArray()))
}