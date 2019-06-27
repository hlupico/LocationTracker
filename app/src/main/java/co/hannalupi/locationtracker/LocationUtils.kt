package co.hannalupi.locationtracker

import com.google.android.gms.location.LocationRequest

class LocationUtils {
    companion object {

        val REQUEST_INTERVAL = 30_000L // 30 Seconds
        val REQUEST_FASTEST_INTERVAL = 15_000L // 15 Seconds
        val REQUEST_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY


        fun createLocationRequest() : LocationRequest {
            return LocationRequest()
                .setInterval(REQUEST_INTERVAL)
                .setFastestInterval(REQUEST_FASTEST_INTERVAL)
                .setPriority(REQUEST_PRIORITY)
        }
    }
}