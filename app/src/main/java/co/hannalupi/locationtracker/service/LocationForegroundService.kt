package co.hannalupi.locationtracker.service

import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import co.hannalupi.locationtracker.LocationUtils
import co.hannalupi.locationtracker.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class LocationForegroundService : Service() {

    private val TAG = LocationForegroundService::class.java.simpleName

    private val ID_LOCATION_SERVICE = 123
    private val NOTIFICATION_CHANNEL_ID = "locationForegroundService"
    private val NOTIFICATION_CHANNEL_NAME = "channel_location_service"

    private lateinit var binder : IBinder
    private lateinit var locationCallback : LocationServiceCallback
    private lateinit var fusedClient : FusedLocationProviderClient

    override fun onCreate() {

        locationCallback = LocationServiceCallback()
        binder = LocationBinder()

        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY // Tells system not to recreate service once killed
    }

    override fun onBind(intent: Intent?): IBinder? {
        stopForeground(true)
        return binder
    }

    override fun onRebind(intent: Intent?) {
        stopForeground(true)
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        startForeground(ID_LOCATION_SERVICE, getNotification())
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    fun registerLocationUpdates() {
        startService(Intent(applicationContext, LocationForegroundService::class.java))

        try {
            fusedClient = LocationServices.getFusedLocationProviderClient(this)
            getLastLocation()
            fusedClient.requestLocationUpdates(LocationUtils.createLocationRequest(), locationCallback, Looper.myLooper())
        }
        catch(e : SecurityException) {
            Log.d(TAG, "Register for location updated unsuccessful. " + e.toString())
        }
    }

    fun unregisterLocationUpdates() {
        try {
            fusedClient.removeLocationUpdates(locationCallback)
            stopSelf()
        }
        catch(e : SecurityException) {
            Log.d(TAG, e.toString())
        }
    }

    private fun getLastLocation() {
        if(::fusedClient.isInitialized) {
            try {
                fusedClient.lastLocation.addOnCompleteListener{ task ->
                    if(task.isSuccessful && task.result != null) {
                        onNewLocation(task.result)
                    }
                }
            }
            catch(e : SecurityException) {
                Log.d(TAG, e.toString())
            }
        }
    }

    inner class LocationServiceCallback : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            result?.let{
                onNewLocation(it.locations[0])
            }
        }
    }

    private fun onNewLocation(location : Location?) {
        Log.d(TAG, location.toString())
    }

    inner class LocationBinder : Binder() {
        fun getService(): LocationForegroundService {
            return this@LocationForegroundService
        }
    }

    private fun getNotification() : Notification {
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        val notification : Notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getText(co.hannalupi.locationtracker.R.string.notification_title))
            .setContentText(getText(co.hannalupi.locationtracker.R.string.notification_title))
            .setContentIntent(contentIntent)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setSmallIcon(co.hannalupi.locationtracker.R.drawable.ic_location_on_black_24dp)
            .setOngoing(true)
            .build()

        return notification
    }

    // Tell SystemManager a channel has been created for
    // Create Channel for O and higher, pass Channel to Manager
    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}