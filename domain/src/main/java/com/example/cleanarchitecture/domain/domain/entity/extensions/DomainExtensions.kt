package com.example.cleanarchitecture.domain.domain.entity.extensions

fun String.isValidIpv4( ): Boolean {
    val pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$"
    val ipv4Regex = Regex(pattern)
    return ipv4Regex.matches(this)
}

fun String.IsValidIpv4Segment( ): Boolean {
    val ipv4SegmentRegex = "^(\\d{1,3}\\.){3}\\d{1,3}/\\d{1,2}$".toRegex()
    return ipv4SegmentRegex.matches(this)
}


fun String.isValidNameOrLastName(): Boolean {
    val pattern = "^[a-zA-Z\\s]+\$"
    val stringRegex = Regex(pattern)
    return stringRegex.matches(this)
}
