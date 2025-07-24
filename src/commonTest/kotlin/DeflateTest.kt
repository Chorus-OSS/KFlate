import org.chorus_oss.kflate.DeflateCompressor
import org.chorus_oss.kflate.DeflateDecompressor
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeflateTest {
    @Test
    fun compression() {
        val data = "abbabbababaccbababcabcabc".repeat(100000).encodeToByteArray()

        val compressed = DeflateCompressor(9).compress(data)

        val ratio = data.size / compressed.size

        println("Raw: ${data.size}, Compressed: ${compressed.size}, Compression: ${ratio}x")

        assertTrue(data.size > compressed.size)
    }

    @Test
    fun random() {
        val data = (0 until 100_000).map { ('a'..'z').random() }.joinToString("").encodeToByteArray()

        val compressed = DeflateCompressor(9).compress(data)

        val ratio = data.size / compressed.size

        println("Raw: ${data.size}, Compressed: ${compressed.size}, Compression: ${ratio}x")

        assertTrue(data.size > compressed.size)
    }

    @Test
    fun roundTrip() {
        val data = "abbabbababaccbababcabcabc".repeat(100000).encodeToByteArray()

        val compressed = DeflateCompressor(9).compress(data)
        val decompressed = DeflateDecompressor().decompress(compressed)

        assertContentEquals(data, decompressed)
    }

    @Test
    fun randomRoundTrip() {
        val data = (0 until 100_000).map { ('a'..'z').random() }.joinToString("").encodeToByteArray()

        val compressed = DeflateCompressor(9).compress(data)
        val decompressed = DeflateDecompressor().decompress(compressed)

        assertContentEquals(data, decompressed)
    }
}