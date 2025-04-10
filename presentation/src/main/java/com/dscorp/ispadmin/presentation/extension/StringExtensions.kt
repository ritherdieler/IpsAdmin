package com.dscorp.ispadmin.presentation.extension

 fun String.removeSpecialCharacters(): String {
    val pattern = "[^A-Za-z0-9ñÑ ]".toRegex()
    return replace(pattern, "")
}