package org.chorus_oss.kflate

class ZlibCompressor(level: Int): Compressor by platformZlibCompressor(level)

internal expect fun platformZlibCompressor(level: Int): Compressor