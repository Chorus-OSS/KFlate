import org.chorus_oss.kflate.ZlibCompressor
import kotlin.test.Test
import kotlin.test.assertTrue

class ZlibTest {
    @Test
    fun compression() {
        val data = "abbabbababaccbababcabcabc".repeat(100000).encodeToByteArray()

        val compressed = ZlibCompressor(9).compress(data)

        val ratio = data.size / compressed.size

        println("Raw: ${data.size}, Compressed: ${compressed.size}, Compression: ${ratio}x")

        assertTrue(data.size > compressed.size)
    }

    @Test
    fun random() {
        val data = (0 until 100_000).map { ('a'..'z').random() }.joinToString("").encodeToByteArray()

        val compressed = ZlibCompressor(9).compress(data)

        val ratio = data.size / compressed.size

        println("Raw: ${data.size}, Compressed: ${compressed.size}, Compression: ${ratio}x")

        assertTrue(data.size > compressed.size)
    }
}