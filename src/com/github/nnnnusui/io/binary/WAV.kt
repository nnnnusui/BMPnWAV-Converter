package com.github.nnnnusui.io.binary

import java.nio.ByteBuffer

class WAV{
    val MUSIC_DATA_OFFSET = 40
    val header   = bytes(0x52, 0x49, 0x46, 0x46) // "RIFF"
    val fileSize = bytes(4)
    val RIFFType = bytes(0x57, 0x41, 0x56, 0x45) // "WAVE"
    val chunkID  = bytes(0x66, 0x6d, 0x74, 0x20) // "fmt "
    val chunkDataSize = bytes(0x10, 0x00, 0x00, 0x00) // 16 (linearPCM)
    val compressionCode = bytes(0x01, 0x00) // linearPCM
    val numberOfChannels = bytes(0x02, 0x00) // stereo
    val sampleRate = bytes(0x44, 0xac, 0x00, 0x00) //44.1kHz
    val averageBytesPerSecond = bytes(0x10, 0xb1, 0x02, 0x00) //44.1kHz 16bit stereo
    val blockAlign = bytes(0x04, 0x00) //16bit stereo
    val significantBitsPerSample = bytes(0x10, 0x00) //16bit
    val dataHeader = bytes(0x64, 0x61, 0x74, 0x61) // "data"

    fun fromWAV(data: ByteArray)
            = data.copyOfRange(MUSIC_DATA_OFFSET, data.size)
    fun toWAV(data: ByteArray): ByteArray{
        val musicDataSize = data.size + (data.size%4)
        val byteBuffer = ByteBuffer.allocate(MUSIC_DATA_OFFSET + musicDataSize).apply {
            put(header)
            put(fileSize)
            put(RIFFType)
            put(chunkID)
            put(chunkDataSize)
            put(compressionCode)
            put(numberOfChannels)
            put(sampleRate)
            put(averageBytesPerSecond)
            put(blockAlign)
            put(significantBitsPerSample)
            put(dataHeader)

            put(data)
        }
        return byteBuffer.array()
    }
}