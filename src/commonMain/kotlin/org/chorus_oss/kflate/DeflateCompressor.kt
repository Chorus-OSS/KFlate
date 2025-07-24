package org.chorus_oss.kflate

class DeflateCompressor(level: Int) : Compressor by platformDeflateCompressor(level)

internal expect fun platformDeflateCompressor(level: Int): Compressor