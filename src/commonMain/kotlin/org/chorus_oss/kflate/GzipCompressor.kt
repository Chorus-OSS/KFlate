package org.chorus_oss.kflate

class GzipCompressor : Compressor by platformGzipCompressor()

internal expect fun platformGzipCompressor(): Compressor