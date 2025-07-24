package org.chorus_oss.kflate

class ZlibDecompressor : Decompressor by platformZlibDecompressor()

internal expect fun platformZlibDecompressor(): Decompressor