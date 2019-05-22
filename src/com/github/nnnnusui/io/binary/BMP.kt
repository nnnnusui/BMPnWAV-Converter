package com.github.nnnnusui.io.binary

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

class BMPTypeCORE{
    val IMAGE_DATA_OFFSET = 26
    val header    = bytes(0x42, 0x4d) //"BM"
    val fileSize  = bytes(4)
    val reserved1 = bytes(0x00, 0x00)
    val reserved2 = bytes(0x00, 0x00)
    val offset    = bytes(0x00, 0x00, 0x00, 0x00)

    // InformationHeader
        //CORE type
        val headerSize  = bytes(0x0C, 0x00, 0x00, 0x00)
        val width       = bytes(2)
        val height      = bytes(2)
        val plane       = bytes(0x01, 0x00)
        val bitParPixel = bytes(0x18, 0x00)


    fun fromBMP(data: ByteArray)
            = data.copyOfRange(IMAGE_DATA_OFFSET, data.size)
    fun toBMP(data: ByteArray): ByteArray{
        val side = kotlin.math.sqrt(data.size.toDouble() * 16 / 9).roundToInt(); println("side: $side")
        val pxHeight = ((side-1) / 3 + 1)
        var pxWidth  = 1
        while ((pxWidth * 3 + ((pxWidth * 3)%4)) * pxHeight < data.size)
            pxWidth++

        val imageDataSize = (pxWidth * 3 + ((pxWidth * 3)%4)) * pxHeight
        println("size: ${data.size}\nsquare: $pxWidth, $pxHeight -> ${imageDataSize}")

        val byteBuffer = ByteBuffer.allocate(IMAGE_DATA_OFFSET + imageDataSize).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            put(header,     0, 2)
            put(fileSize,   0, 4) // fileSize
            put(reserved1,  0, 2)
            put(reserved2,  0, 2)
            put(offset,     0, 4) // offsetSize

            put(headerSize ,0, 4)
            put(width,      0, 2)
            put(height,     0, 2)
            position(22)
            put(plane,      0, 2)
            put(bitParPixel,0, 2)

            putInt( 2, limit())
            putInt(10, IMAGE_DATA_OFFSET)
            putInt(18, pxWidth)
            putInt(20, pxHeight)

            put(data)
        }
        return byteBuffer.array()
    }
}