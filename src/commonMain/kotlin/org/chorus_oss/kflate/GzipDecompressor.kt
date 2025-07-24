package org.chorus_oss.kflate

class GzipDecompressor : Decompressor by platformGzipDecompressor()

internal expect fun platformGzipDecompressor(): Decompressor