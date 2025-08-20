package com.acnteam.crashybird.utils

fun String.center(length: Int, padChar: Char = ' '): String {
    if (this.length >= length) return this.take(length)
    val totalPadding = length - this.length
    val padStart = totalPadding / 2
    val padEnd = totalPadding - padStart
    return padChar.toString().repeat(padStart) + this + padChar.toString().repeat(padEnd)
}