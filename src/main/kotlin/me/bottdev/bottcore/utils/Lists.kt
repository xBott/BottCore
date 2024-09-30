package me.bottdev.bottcore.utils

@Suppress("unused")
fun <T> List<T>.splitListIntoParts(parts: Int): List<List<T>> {

    if (parts <= 0) throw IllegalArgumentException("Parts count must be greater than zero")

    val chunkSize = this.size / parts
    val remainder = this.size % parts

    var startIndex = 0
    val result = mutableListOf<List<T>>()

    for (i in 0..<parts) {
        val endIndex = startIndex + chunkSize + if (i < remainder) 1 else 0
        result.add(this.subList(startIndex, endIndex))
        startIndex = endIndex
    }

    return result
}