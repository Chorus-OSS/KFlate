import org.chorus_oss.kflate.Deflater
import kotlin.test.Test
import kotlin.test.assertTrue

class DeflaterTest {
    @Test
    fun deflaterCompressionTest() {
        val data = "abbabbababaccbababcabcabc".repeat(100000).encodeToByteArray()

        val compressed = Deflater(9).compress(data)

        val ratio = data.size / compressed.size

        println("Raw: ${data.size}, Compressed: ${compressed.size}, Compression: ${ratio}x")

        assertTrue(data.size > compressed.size)
    }
}