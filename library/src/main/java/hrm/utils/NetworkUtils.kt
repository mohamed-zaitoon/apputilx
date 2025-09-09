package hrm.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

internal object NetworkUtils {
    private lateinit var connectivityManager: ConnectivityManager
    private val listeners = mutableListOf<(Boolean) -> Unit>()

    var isConnected: Boolean = false
        private set

    fun initialize(context: Context) {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // احسب الاتصال الحالي قبل ما يبدأ الـ callback
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(
            request,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isConnected = true
                    notifyListeners(true)
                }

                override fun onLost(network: Network) {
                    isConnected = false
                    notifyListeners(false)
                }
            }
        )
    }

    private fun notifyListeners(status: Boolean) {
        listeners.forEach { it(status) }
    }

    fun addConnectionListener(listener: (Boolean) -> Unit) {
        listeners.add(listener)
        listener(isConnected) // نبلغه بالحالة الحالية فورًا
    }
}