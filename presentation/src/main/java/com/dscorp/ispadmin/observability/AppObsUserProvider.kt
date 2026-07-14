package com.dscorp.ispadmin.observability

import com.dscorp.ispadmin.data.repository.IRepository

class AppObsUserProvider(
    private val repository: IRepository
) : ObsUserProvider {
    override fun currentUser(): Map<String, Any?>? {
        val session = runCatching { repository.getUserSession() }.getOrNull() ?: return null
        return mapOf(
            "id" to session.id,
            "username" to session.username,
            "name" to session.toString(),
            "email" to session.email,
            "type" to session.typeAsString()
        )
    }
}
