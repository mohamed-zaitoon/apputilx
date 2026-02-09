package apputilx.helpers

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal object Network : DefaultLifecycleObserver {

    private lateinit var connectivityManager: ConnectivityManager
    private val listeners = mutableListOf<(Boolean) -> Unit>()
    private var initialized = false
    private var registeredCallback = false
    private val callback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateConnection(true)
            }

            override fun onLost(network: Network) {
                updateConnection(false)
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

        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // pre-populate state
        updateCurrentConnectionState()

        // observe app lifecycle to register/unregister callbacks safely
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        registerCallbackIfNeeded()
    }

    override fun onStart(owner: LifecycleOwner) {
        registerCallbackIfNeeded()
    }

    override fun onStop(owner: LifecycleOwner) {
        unregisterCallback()
    }

    /**
     * Add a listener to be notified when the network connection changes.
     * The listener is immediately invoked with the current state.
     */
    fun addConnectionListener(listener: (Boolean) -> Unit) {
        listeners.add(listener)
        listener(isConnected)
    }
    
    /**
     * Remove a previously added network connection listener.
     */
    fun removeConnectionListener(listener: (Boolean) -> Unit) {
        listeners.remove(listener)
    }

    // --------------------------------------------------
    // Internal helpers
    // --------------------------------------------------

    private fun updateCurrentConnectionState() {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        isConnected =
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun updateConnection(status: Boolean) {
        if (isConnected == status) return
        isConnected = status
        notifyListeners(status)
    }

    private fun registerCallbackIfNeeded() {
        if (registeredCallback) return
        connectivityManager.registerNetworkCallback(request, callback)
        registeredCallback = true
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
}
