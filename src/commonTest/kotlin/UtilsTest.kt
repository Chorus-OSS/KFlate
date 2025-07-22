import org.chorus_oss.kflate.utils.chunk
import org.chorus_oss.kflate.utils.chunkExact
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {
    @Test
    fun byteArrayChunkTest() {
        val bytes = ByteArray(23) { it.toByte() }

        val chunks = bytes.chunk(8)

        assertEquals(3, chunks.size)

        assertEquals(8, chunks[0].size)
        assertEquals(8, chunks[1].size)
        assertEquals(7, chunks[2].size)
    }

    @Test
    fun byteArrayChunkExactTest() {
        val bytes = ByteArray(23) { it.toByte() }

        val (chunks, remainder) = bytes.chunkExact(8)

        assertEquals(2, chunks.size)

        assertEquals(8, chunks[0].size)
        assertEquals(8, chunks[1].size)

        assertEquals(7, remainder.size)
    }

    @Test
    fun byteArrayChunkNoRemainderTest() {
        val bytes = ByteArray(16) { it.toByte() }

        val chunks = bytes.chunk(8)

        assertEquals(2, chunks.size)

        assertEquals(8, chunks[0].size)
        assertEquals(8, chunks[1].size)
    }

    @Test
    fun byteArrayChunkEmptyTest() {
        val bytes = ByteArray(0)

        val chunks = bytes.chunk(8)

        assertEquals(0, chunks.size)
    }

    @Test
    fun byteArrayChunkExactEmptyTest() {
        val bytes = ByteArray(0)

        val (chunks, remainder) = bytes.chunkExact(8)

        assertEquals(0, chunks.size)
        assertEquals(0, remainder.size)
    }

    @Test
    fun largeByteArrayChunkExactTest() {
        val bytes = ByteArray(1024 * 1024)

        val (chunks, remainder) = bytes.chunkExact(5552 * 4)

        assertEquals(47, chunks.size)
        assertEquals(4800, remainder.size)
    }
}