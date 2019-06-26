package co.hannalupi.locationtracker.service

import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import co.hannalupi.locationtracker.MainActivity
import co.hannalupi.locationtracker.R
import com.google.android.gms.location.LocationRequest

class LocationForegroundService : Service() {

    private val ID_LOCATION_SERVICE = 123
    private val NOTIFICATION_CHANNEL_ID = "locationForegroundService"
    private val NOTIFICATION_CHANNEL_NAME = "channel_location_service"

    // TODO: Add location request constants
    private val binder = LocationBinder()


    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // START_STICKY - Used explicitly for services started and stopped as needed
        return START_STICKY
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

    override fun onDestroy() {
        super.onDestroy()
    }

    fun registerLocationUpdates() {

    }

    fun unregisterLocationUpdated() {

    }

    fun createLocationRequest(): LocationRequest {
        // TODO: Update
        return LocationRequest.create()
    }

    inner class LocationBinder : Binder() {
        fun getService(): LocationForegroundService {
            return this@LocationForegroundService
        }

        fun getLocationRequest() : LocationRequest {
            return this@LocationForegroundService.createLocationRequest()
        }
    }

    private fun getNotification() : Notification {
        //If needed creates notification channel
        createNotificationChannel()

        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        val notification : Notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_title))
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_location_on_black_24dp)
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