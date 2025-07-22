import org.chorus_oss.kflate.lz77.LZ77Common
import org.chorus_oss.kflate.lz77.LZ77HashChain
import org.chorus_oss.kflate.lz77.LZ77Token
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class LZ77Test {
    @Test
    fun roundTrip() {
        val dataString = "abracadabraabracadabra"
        val data = dataString.encodeToByteArray()
        val compressor = LZ77HashChain(258, 3, 128, 4096)

        val decompressed = mutableListOf<Byte>()
        compressor.compress(data) { token ->
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
    }

    @Test
    fun largeRoundTrip() {
        val data = ByteArray(10_000_000) { ('a'..'z').random().code.toByte() }
        val compressor = LZ77HashChain(258, 3, 128, 4096)

        val decompressed = mutableListOf<Byte>()
        compressor.compress(data) { token ->
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
        val decompressedData = decompressed.toByteArray()

        assertTrue(decompressedData.contentEquals(data))
    }

    @Test
    fun hashCollision() {
        val first = "utv".encodeToByteArray()
        val second = "cem".encodeToByteArray()

        val firstHash = LZ77Common.hash(LZ77Common.readU24(first, 0), 15)
        val secondHash = LZ77Common.hash(LZ77Common.readU24(second, 0), 15)

        assertEquals(firstHash, secondHash)
    }
}