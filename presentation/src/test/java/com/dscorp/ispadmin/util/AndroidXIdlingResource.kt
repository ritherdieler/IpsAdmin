/*
package com.dscorp.ispadmin.util

import com.jakewharton.espresso.OkHttp3IdlingResource
import android.support.test.espresso.IdlingResource as SupportIdlingResource
import android.support.test.espresso.IdlingResource.ResourceCallback as SupportResourceCallback
import androidx.test.espresso.IdlingResource as AndroidXIdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback as AndroidXResourceCallback

*/
/**
 * Allows [SupportIdlingResource] and [AndroidXIdlingResource] to exist in the same module.
 *
 * This will be needed until [OkHttp3IdlingResource] is migrated to AndroidX.
 * https://github.com/JakeWharton/okhttp-idling-resource/issues/19#issuecomment-518369287
 *//*

internal fun Idling.asAndroidX(): AndroidXIdlingResource = AndroidXIdlingResource(this)

class AndroidXIdlingResource(private val delegate: SupportIdlingResource) : AndroidXIdlingResource {

    override fun getName(): String = delegate.name

    override fun isIdleNow(): Boolean = delegate.isIdleNow

    override fun registerIdleTransitionCallback(callback: AndroidXResourceCallback) = delegate
        .registerIdleTransitionCallback(callback.asSupport())
}

class SupportResourceCallback(private val delegate: AndroidXResourceCallback) : SupportResourceCallback {
    override fun onTransitionToIdle() = delegate.onTransitionToIdle()

}

internal fun AndroidXResourceCallback.asSupport(): SupportResourceCallback = SupportResourceCallback(this)*/
