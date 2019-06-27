package co.hannalupi.locationtracker

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.hannalupi.locationtracker.service.LocationForegroundService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = MainActivity::class.java.simpleName
    private val SHARED_PREFS = "shared_prefs"
    private val TRACKING_ENABLED = "tracking_enabled"

    private val LOCATION_ACCESS_REQUEST = 1

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
        if (::sharedPrefs.isInitialized) {
            sharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
        }

        if (isBound) {
            unbindService(serviceConnection)
            service = null
            isBound = false
        }
        super.onStop()
    }

    fun startTracking(view: View) {
        Log.d(TAG, "startTracking()")
        startActivityForResult(Intent(this, LocationAccessActivity::class.java), LOCATION_ACCESS_REQUEST)
    }

    fun stopTracking(view : View) {
        Log.d(TAG, "stopTracking()")
        service?.unregisterLocationUpdates()
        updateUserEnabledTracking(false)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(TAG, "onSharedPreferencesChanged")
        //TODO: Revisit this, maybe this should save this value to a member variable
        updateButtonEnabledState(userEnabledTracking())
        if(userEnabledTracking()) {
            service?.registerLocationUpdates()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOCATION_ACCESS_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Location ENABLED!! YAY", Toast.LENGTH_LONG).show()
                    updateUserEnabledTracking(true)
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, "Location not enabled", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

