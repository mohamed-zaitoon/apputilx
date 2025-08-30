package hrm

import android.app.Activity
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

internal object NetworkUtils {
    private lateinit var connectivityManager: ConnectivityManager
    private var listeners = mutableListOf<(Boolean) -> Unit>()
    var isConnected: Boolean = false
        private set

    fun initialize(activity: Activity) {
        connectivityManager = activity.getSystemService(ConnectivityManager::class.java)
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected = true
                notifyListeners(true)
            }

            override fun onLost(network: Network) {
                isConnected = false
                notifyListeners(false)
            }
        })
    }

    private fun notifyListeners(status: Boolean) {
        listeners.forEach { it(status) }
    }

    fun addConnectionListener(listener: (Boolean) -> Unit) {
        listeners.add(listener)
        listener(isConnected)
    }
}