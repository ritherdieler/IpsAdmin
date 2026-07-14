package com.dscorp.ispadmin.observability

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

class ObservabilityActivityTracker(
    private val recorder: ObservabilityScreenRecorder?,
    private val uiTracker: ObservabilityUiTracker? = null
) : Application.ActivityLifecycleCallbacks {

    @Volatile
    private var currentActivity: WeakReference<Activity>? = null

    fun currentActivity(): Activity? = currentActivity?.get()

    override fun onActivityResumed(activity: Activity) {
        currentActivity = WeakReference(activity)
        recorder?.attach(activity)
        uiTracker?.attach(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivity?.get() === activity) {
            currentActivity = null
        }
        uiTracker?.detach()
        recorder?.detach()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}
}
