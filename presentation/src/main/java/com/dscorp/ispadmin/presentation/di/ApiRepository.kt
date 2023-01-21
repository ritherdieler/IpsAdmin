package com.dscorp.ispadmin.presentation.di

import org.koin.dsl.module

val apiModule = module {
    single { providesApi(get()) }
}