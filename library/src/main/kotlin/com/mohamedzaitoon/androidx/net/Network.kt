package com.mohamedzaitoon.androidx.net

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.mohamedzaitoon.androidx.AppUtilX
import java.net.Inet4Address
import java.net.NetworkInterface

@SuppressLint("NewApi", "InlinedApi")
object Network : DefaultLifecycleObserver {

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var appContext: Context
    private val listeners = mutableListOf<(Boolean) -> Unit>()
    private var initialized = false
    private var registeredCallback = false
    private val callback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                refreshConnectionState()
            }

            override fun onLost(network: Network) {
                refreshConnectionState()
            }
        }
    }
    private val request by lazy {
        NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
    }

    val isConnected: Boolean
        get() = hasValidatedInternet()

    fun initialize(context: Context) {
        if (initialized) return
        initialized = true
        appContext = context.applicationContext

        connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE)
            as? ConnectivityManager
            ?: return

        updateCurrentConnectionState()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        registerMonitorIfNeeded()
    }

    override fun onStart(owner: LifecycleOwner) {
        updateCurrentConnectionState()
        registerMonitorIfNeeded()
    }

    override fun onStop(owner: LifecycleOwner) {
        unregisterMonitor()
    }

    fun addConnectionListener(listener: (Boolean) -> Unit) {
        updateCurrentConnectionState()
        listeners.add(listener)
        listener(isConnected)
    }

    fun removeConnectionListener(listener: (Boolean) -> Unit) {
        listeners.remove(listener)
    }

    fun hasValidatedInternet(): Boolean {
        val capabilities = currentCapabilities() ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun activeTransport(): String {
        val capabilities = currentCapabilities() ?: return "NONE"
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "CELLULAR"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETHERNET"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "BLUETOOTH"
            else -> "UNKNOWN"
        }
    }

    fun isWifiConnected(): Boolean =
        hasTransport(NetworkCapabilities.TRANSPORT_WIFI)

    fun isCellularConnected(): Boolean =
        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

    fun isEthernetConnected(): Boolean =
        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

    fun isVpnConnected(): Boolean =
        hasTransport(NetworkCapabilities.TRANSPORT_VPN)

    fun isConnectionMetered(): Boolean {
        if (!initialized) return false
        return connectivityManager.isActiveNetworkMetered
    }

    fun getIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.hostAddress
                    }
                }
            }
        } catch (_: Exception) {
            // Guard
        }
        return null
    }

    private var _lastConnectionState = false

    private fun updateCurrentConnectionState() {
        _lastConnectionState = hasValidatedInternet()
    }

    private fun refreshConnectionState() {
        val previous = _lastConnectionState
        updateCurrentConnectionState()
        if (previous != _lastConnectionState) {
            notifyListeners(_lastConnectionState)
        }
    }

    private fun registerCallbackIfNeeded() {
        if (registeredCallback) return
        try {
            connectivityManager.registerNetworkCallback(request, callback)
            registeredCallback = true
        } catch (_: Exception) {
            // Guard
        }
    }

    private fun registerMonitorIfNeeded() {
        registerCallbackIfNeeded()
    }

    private fun unregisterMonitor() {
        unregisterCallback()
    }

    private fun unregisterCallback() {
        if (!registeredCallback) return
        try {
            connectivityManager.unregisterNetworkCallback(callback)
        } catch (_: Exception) {
            // ignore
        }
        registeredCallback = false
    }

    private fun notifyListeners(status: Boolean) {
        listeners.toList().forEach { it(status) }
    }

    private fun currentCapabilities(): NetworkCapabilities? {
        if (!initialized) {
            val ctx = try { AppUtilX.ctx() } catch (_: Exception) { null }
            if (ctx != null) initialize(ctx) else return null
        }
        val network = connectivityManager.activeNetwork ?: return null
        return connectivityManager.getNetworkCapabilities(network)
    }

    private fun hasTransport(transport: Int): Boolean {
        return currentCapabilities()?.hasTransport(transport) == true
    }
}
