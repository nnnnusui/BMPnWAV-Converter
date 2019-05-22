package com.github.nnnnusui.io.binary

fun bytes(vararg bytes: Int)
        = byteArrayOf(*bytes.map { it.toByte() }.toByteArray())
fun bytes(size: Int)
        = ByteArray(size)