package co.hannalupi.locationtracker

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.tasks.Task

/**
 * Headless Activity that checks if the user has appropriate location settings and permissions enabled.
 *
 * @return RESULT_OK - When both location settings and permissions are enabled.
 * @return RESULT_CANCELED - When either location settings or permissions not enabled.
 */
class LocationAccessActivity : AppCompatActivity() {

    private val TAG = LocationAccessActivity::class.java.simpleName
    private val REQUEST_CHECK_SETTINGS = 123
    private val REQUEST_CODE_PERMISSION = 456

    private val locationRequest = LocationUtils.createLocationRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ensureLocationSettings()
    }

    private fun ensureLocationSettings() {
        val builder =
            LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val task: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(this)
            .checkLocationSettings(builder.build())

        task.addOnCompleteListener {
            try {
                ensurePermissionsEnabled()
            }
            catch (e : ApiException) {
                when(e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            // Result for started resolution is handled in onActivityResult
                            (e as ResolvableApiException).startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                        } catch(sendException : IntentSender.SendIntentException) {
                            // Do nothing, Should not happen
                        } catch (castException : ClassCastException) {
                            // Do nothing, Should not happen
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // User denied enabling location service
                        onLocationInaccesible()
                    }
                }
            }
        }
    }

    /**
     * Current case checks for explicit enabled/disabled location service.
     *
     * Additional location provider info can be retrieved from LocationSettingsState.fromIntent(data)
     * https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {
                        // User enabled require location service
                        ensurePermissionsEnabled()
                    }
                    Activity.RESULT_CANCELED -> {
                        // User did not enabled required location service
                        onLocationInaccesible()
                    }
                }
            }
        }
    }

    private fun ensurePermissionsEnabled() {
        if (PermissionUtils.checkPermission(this, PermissionUtils.LOCATION_PERMISSION)) {
            onLocationAccessible()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(PermissionUtils.LOCATION_PERMISSION), REQUEST_CODE_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onLocationAccessible()
                } else {
                    onLocationInaccesible()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun onLocationAccessible() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun onLocationInaccesible() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}