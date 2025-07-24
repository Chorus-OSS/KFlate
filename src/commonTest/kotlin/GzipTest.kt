import kotlinx.io.bytestring.encodeToByteString
import org.chorus_oss.kflate.GzipCompressor
import org.chorus_oss.kflate.GzipDecompressor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GzipTest {
    @Test
    fun compression() {
        val data = "abbabbababaccbababcabcabc".repeat(100000).encodeToByteString()

        val compressed = GzipCompressor().compress(data)

        val ratio = data.size / compressed.size

        println("Raw: ${data.size}, Compressed: ${compressed.size}, Compression: ${ratio}x")

        assertTrue(data.size > compressed.size)
    }

    @Test
    fun random() {
        val data = (0 until 100_000).map { ('a'..'z').random() }.joinToString("").encodeToByteString()

        val compressed = GzipCompressor().compress(data)

        val ratio = data.size / compressed.size

        println("Raw: ${data.size}, Compressed: ${compressed.size}, Compression: ${ratio}x")

        assertTrue(data.size > compressed.size)
    }

    @Test
    fun roundTrip() {
        val data = "abbabbababaccbababcabcabc".repeat(100000).encodeToByteString()

        val compressed = GzipCompressor().compress(data)
        val decompressed = GzipDecompressor().decompress(compressed)

        assertEquals(data, decompressed)
    }

    @Test
    fun randomRoundTrip() {
        val data = (0 until 100_000).map { ('a'..'z').random() }.joinToString("").encodeToByteString()

        val compressed = GzipCompressor().compress(data)
        val decompressed = GzipDecompressor().decompress(compressed)

        assertEquals(data, decompressed)
    }
}