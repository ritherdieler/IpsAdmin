package com.dscorp.ispadmin.domain.model

data class PagedResult<T>(
    val items: List<T> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val total: Long = 0,
    val totalPages: Int = 0,
) {
    val canLoadMore: Boolean
        get() = page + 1 < totalPages
}
