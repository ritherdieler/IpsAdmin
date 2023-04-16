package com.dscorp.ispadmin.presentation.di.app

import android.content.Context

class ResourceProviderImpl(private val context: Context) : ResourceProvider {
    override fun getString(resourceId: Int): String {
        return context.getString(resourceId)
    }
}