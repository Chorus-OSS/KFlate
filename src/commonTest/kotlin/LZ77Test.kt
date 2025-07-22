import org.chorus_oss.kflate.lz77.LZ77HashChain
import org.chorus_oss.kflate.lz77.LZ77Token
import kotlin.test.Test
import kotlin.test.assertEquals

class LZ77Test {
    @Test
    fun `round-trip`() {
        val dataString = "abracadabraabracadabra"
        val data = dataString.encodeToByteArray()
        val compressor = LZ77HashChain(258, 3, 128, 4096)

        val compressed = compressor.compress(data)

        val decompressed = mutableListOf<Byte>()
        for (token in compressed) {
            when (token) {
                is LZ77Token.Literal -> {
                    decompressed += token.value
                }
                is LZ77Token.Match -> {
                    val offset = token.offset.toInt()
                    val length = token.length.toInt()
                    val start = decompressed.size - offset

                    repeat(length) {
                        decompressed += decompressed[start + it]
                    }
                }
            }
        }

        val decompressedString = decompressed.toByteArray().decodeToString()

        assertEquals(dataString, decompressedString)

        println(dataString)
        println(compressed)
        println(decompressedString)
    }
}