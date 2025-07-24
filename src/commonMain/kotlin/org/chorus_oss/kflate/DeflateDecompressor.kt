package org.chorus_oss.kflate

class DeflateDecompressor : Decompressor by platformDeflateDecompressor()

internal expect fun platformDeflateDecompressor(): Decompressor