package apputilx.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

@SuppressLint("NewApi", "InlinedApi")
internal object Network : DefaultLifecycleObserver {

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

    /**
     * Indicates whether the device is currently connected to the internet.
     */
    var isConnected: Boolean = false
        private set

    /**
     * Initialize the network monitoring.
     * Must be called once from Application.onCreate().
     */
    fun initialize(context: Context) {
        if (initialized) return
        initialized = true
        appContext = context.applicationContext

        connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE)
            as? ConnectivityManager
            ?: return

        // pre-populate state
        updateCurrentConnectionState()

        // observe app lifecycle to register/unregister callbacks safely
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

    /**
     * Add a listener to be notified when the network connection changes.
     * The listener is immediately invoked with the current state.
     */
    fun addConnectionListener(listener: (Boolean) -> Unit) {
        updateCurrentConnectionState()
        listeners.add(listener)
        listener(isConnected)
    }
    
    /**
     * Remove a previously added network connection listener.
     */
    fun removeConnectionListener(listener: (Boolean) -> Unit) {
        listeners.remove(listener)
    }

    /**
     * Indicates whether the current network has been validated by the system.
     */
    fun hasValidatedInternet(): Boolean {
        val capabilities = currentCapabilities() ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Return the active transport name: WIFI, CELLULAR, ETHERNET, VPN, BLUETOOTH, UNKNOWN, or NONE.
     */
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

    // --------------------------------------------------
    // Internal helpers
    // --------------------------------------------------

    private fun updateCurrentConnectionState() {
        isConnected = hasValidatedInternet()
    }

    private fun refreshConnectionState() {
        val previous = isConnected
        updateCurrentConnectionState()
        if (previous != isConnected) {
            notifyListeners(isConnected)
        }
    }

    private fun registerCallbackIfNeeded() {
        if (registeredCallback) return
        connectivityManager.registerNetworkCallback(request, callback)
        registeredCallback = true
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
        // Iterate on a copy to avoid ConcurrentModificationException
        listeners.toList().forEach { it(status) }
    }

    private fun currentCapabilities(): NetworkCapabilities? {
        if (!initialized) return null
        val network = connectivityManager.activeNetwork ?: return null
        return connectivityManager.getNetworkCapabilities(network)
    }

    private fun hasTransport(transport: Int): Boolean {
        return currentCapabilities()?.hasTransport(transport) == true
    }
}
