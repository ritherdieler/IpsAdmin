package com.dscorp.ispadmin.domain.usecase.subscription

import com.dscorp.ispadmin.data.repository.IRepository
import com.dscorp.ispadmin.domain.model.PagedResult
import com.dscorp.ispadmin.domain.model.SubscriptionResume

class SearchSubscriptionsUseCase(private val repository: IRepository) {

    suspend operator fun invoke(
        query: String,
        status: String? = null,
        page: Int = 0,
        size: Int = DEFAULT_PAGE_SIZE,
    ): Result<PagedResult<SubscriptionResume>> = runCatching {
        repository.searchSubscriptions(query.trim(), status, page, size)
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
