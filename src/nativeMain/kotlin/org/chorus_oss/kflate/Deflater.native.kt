package org.chorus_oss.kflate

actual fun Deflater(level: Int): Deflater = NativeDeflater(level)