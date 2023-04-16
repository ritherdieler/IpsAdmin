package com.dscorp.ispadmin.presentation.di.modules

import com.dscorp.ispadmin.presentation.di.app.ResourceProvider
import com.dscorp.ispadmin.presentation.di.app.ResourceProviderImpl
import org.koin.dsl.module

val applicationModule = module {
    single<ResourceProvider> { ResourceProviderImpl(get()) }
}