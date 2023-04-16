package com.dscorp.ispadmin.presentation.di.app

interface ResourceProvider {
    fun getString(resourceId: Int): String
}