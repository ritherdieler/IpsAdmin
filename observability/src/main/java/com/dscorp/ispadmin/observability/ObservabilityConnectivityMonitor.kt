package com.dscorp.ispadmin.observability

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network

class ObservabilityConnectivityMonitor(
    private val context: Context,
    private val clientProvider: Lazy<ObservabilityClient>,
    private val tracerProvider: Lazy<ObservabilityTracer>
) {

    fun register() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return
        runCatching {
            connectivityManager.registerDefaultNetworkCallback(
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        runCatching { clientProvider.value.flush() }
                        runCatching { tracerProvider.value.flush() }
                    }
                }
            )
        }
    }
}
