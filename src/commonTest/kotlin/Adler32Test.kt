import org.chorus_oss.kflate.adler32.Adler32
import kotlin.test.Test
import kotlin.test.assertEquals

class Adler32Test {
    @Test
    fun zeroes() {
        assertEquals(1u, Adler32.checksum(byteArrayOf()))
        assertEquals(1u or (1u shl 16), Adler32.checksum(byteArrayOf(0)))
        assertEquals(1u or (2u shl 16), Adler32.checksum(byteArrayOf(0, 0)))
        assertEquals(0x00640001u, Adler32.checksum(ByteArray(100) { 0 }))
        assertEquals(0x04000001u, Adler32.checksum(ByteArray(1024) { 0 }))
        assertEquals(0x00f00001u, Adler32.checksum(ByteArray(1024 * 1024) { 0 }))
    }

    @Test
    fun ones() {
        assertEquals(0x79a6fc2eu, Adler32.checksum(ByteArray(1024) { 0xFF.toByte() }))
        assertEquals(0x8e88ef11u, Adler32.checksum(ByteArray(1024 * 1024) { 0xFF.toByte() }))
    }

    @Test
    fun mixed() {
        assertEquals(2u or (2u shl 16), Adler32.checksum(byteArrayOf(1)))
        assertEquals(41u or (41u shl 16), Adler32.checksum(byteArrayOf(40)))
        assertEquals(0xd5009ab1u, Adler32.checksum(ByteArray(1024 * 1024) { 0xA5.toByte() }))
    }

    @Test
    fun wiki() {
        assertEquals(0x11E60398u, Adler32.checksum("Wikipedia".encodeToByteArray()))
    }

    @Test
    fun resume() {
        val adler = Adler32()
        adler.write(ByteArray(1024) { 0xFF.toByte() })
        val partial = adler.checksum
        assertEquals(0x79a6fc2eu, partial)
        adler.write(ByteArray(1024 * 1024 - 1024) { 0xFF.toByte() })
        assertEquals(0x8e88ef11u, adler.checksum)

        val adler2 = Adler32(partial)
        adler2.write(ByteArray(1024 * 1024 - 1024) { 0xFF.toByte() })
        assertEquals(0x8e88ef11u, adler2.checksum)
    }
}