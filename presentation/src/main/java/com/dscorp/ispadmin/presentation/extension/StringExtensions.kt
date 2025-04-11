package com.dscorp.ispadmin.presentation.extension

 fun String.removeSpecialCharacters(): String {
    val pattern = "[^A-Za-z0-9ñÑ ]".toRegex()
    return replace(pattern, "")
}
fun String.removeAccents(): String {
    val accents = mapOf(
        'á' to 'a', 'é' to 'e', 'í' to 'i', 'ó' to 'o', 'ú' to 'u',
        'Á' to 'A', 'É' to 'E', 'Í' to 'I', 'Ó' to 'O', 'Ú' to 'U',
        'ñ' to 'n', 'Ñ' to 'N'
    )
    return fold("") { acc, c -> acc + (accents[c] ?: c) }
}