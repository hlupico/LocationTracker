package co.hannalupi.locationtracker

import com.google.android.gms.location.LocationRequest

class LocationUtils {
    companion object {

        val REQUEST_INTERVAL = 120_000L // 2 Minutes
        val REQUEST_FASTEST_INTERVAL = 60_000L // 1 Minute
        val REQUEST_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY


        fun createLocationRequest() : LocationRequest {
            return LocationRequest()
                .setInterval(REQUEST_INTERVAL)
                .setFastestInterval(REQUEST_FASTEST_INTERVAL)
                .setPriority(REQUEST_PRIORITY)
        }
    }
}