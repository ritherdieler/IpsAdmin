package com.dscorp.ispadmin.observability

import com.google.firebase.crashlytics.FirebaseCrashlytics

class FirebaseObsCrashReporter : ObsCrashReporter {
    override fun recordException(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }
}
