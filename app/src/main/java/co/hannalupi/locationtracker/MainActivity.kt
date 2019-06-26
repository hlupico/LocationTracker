package co.hannalupi.locationtracker

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import co.hannalupi.locationtracker.service.LocationForegroundService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = MainActivity::class.java.simpleName
    private val SHARED_PREFS = "shared_prefs"
    private val TRACKING_ENABLED = "tracking_enabled"

    private lateinit var sharedPrefs : SharedPreferences

    private var isBound = false
    private var serviceConnection = getServiceConnection()
    private var service : LocationForegroundService? = null

    private var startBtn : Button? = null
    private var stopBtn : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        startBtn = findViewById(R.id.btn_start)
        stopBtn = findViewById(R.id.btn_stop)

        sharedPrefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()

        if (::sharedPrefs.isInitialized) {
            sharedPrefs.registerOnSharedPreferenceChangeListener(this)
        }

        Intent(this, LocationForegroundService::class.java)
            .apply { bindService(this, serviceConnection, Context.BIND_AUTO_CREATE) }

        updateButtonEnabledState(userEnabledTracking())
    }

    override fun onStop() {
        super.onStop()
        if (::sharedPrefs.isInitialized) {
            sharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
        }
        unbindService(serviceConnection)
        service = null
        isBound = false

        startService(Intent(this, LocationForegroundService::class.java))
    }

    fun startTracking(view : View) {
        Log.d(TAG, "startTracking()")
        updateUserEnabledTracking(true)
    }

    fun stopTracking(view : View) {
        Log.d(TAG, "stopTracking()")
        updateUserEnabledTracking(false)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(TAG, "onSharedPreferencesChanged")
        //TODO: Revisit this, maybe this should save this value to a member variable
        updateButtonEnabledState(userEnabledTracking())
    }

    private fun getServiceConnection() : ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                Log.d(TAG, "Service Connected")
                service = (binder as LocationForegroundService.LocationBinder).getService()
                isBound = true
            }

            // Called when service unexpectedly stops or is killed by the system
            // NOT called when the client unbinds
            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d(TAG, "Service Disconnected")
                service = null
                isBound = false
            }
        }
    }

    private fun userEnabledTracking() : Boolean {
        return sharedPrefs.getBoolean(TRACKING_ENABLED, false)
    }

    private fun updateUserEnabledTracking(enabled : Boolean) {
        sharedPrefs.edit().putBoolean(TRACKING_ENABLED, enabled).apply()
    }

    private fun updateButtonEnabledState(trackingEnabled: Boolean) {
        startBtn?.isEnabled = !trackingEnabled
        stopBtn?.isEnabled = trackingEnabled
    }

//    private fun hasLocationAccess() : Boolean {
//        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED
//    }

//    private fun requestLocationAccess() {
//
//        service?.let{
//            val builder = LocationSettingsRequest.Builder()
//                .addLocationRequest(it.createLocationRequest())
//            val client = LocationServices.getSettingsClient(this)
//            val task : Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//
//            task.addOnSuccessListener { response -> ensurePermissionsEnabled() }
//            task.addOnFailureListener { exception ->
//                if (exception is ResolvableApiException) {
//                    try {
//                        exception.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
//                    } catch (sendEx: IntentSender.SendIntentException) {
//                        Log.d(TAG, sendEx.toString())
//                    }
//                }
//            }
//
//        }
//
//
//    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//
//    }
//
//    private fun ensurePermissionsEnabled() {
//
//    }

}

