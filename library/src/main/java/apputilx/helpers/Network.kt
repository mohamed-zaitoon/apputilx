package apputilx.helpers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
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
    private var registeredReceiver = false
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
    private val receiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val previous = isConnected
                updateCurrentConnectionState()
                if (previous != isConnected) {
                    notifyListeners(isConnected)
                }
            }
        }
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

    // --------------------------------------------------
    // Internal helpers
    // --------------------------------------------------

    private fun updateCurrentConnectionState() {
        isConnected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
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

    private fun registerMonitorIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            registerCallbackIfNeeded()
        } else {
            registerReceiverIfNeeded()
        }
    }

    private fun registerReceiverIfNeeded() {
        if (registeredReceiver) return
        @Suppress("DEPRECATION")
        appContext.registerReceiver(
            receiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        registeredReceiver = true
    }

    private fun unregisterMonitor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            unregisterCallback()
        } else {
            unregisterReceiver()
        }
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

    private fun unregisterReceiver() {
        if (!registeredReceiver) return
        try {
            appContext.unregisterReceiver(receiver)
        } catch (_: Exception) {
            // ignore
        }
        registeredReceiver = false
    }

    private fun notifyListeners(status: Boolean) {
        // Iterate on a copy to avoid ConcurrentModificationException
        listeners.toList().forEach { it(status) }
    }
}
