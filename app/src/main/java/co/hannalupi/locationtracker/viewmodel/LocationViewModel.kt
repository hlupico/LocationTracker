package co.hannalupi.locationtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import co.hannalupi.locationtracker.persistence.Location
import co.hannalupi.locationtracker.persistence.LocationDatabase
import co.hannalupi.locationtracker.repo.LocationRepository

class LocationViewModel(app : Application) : AndroidViewModel(app) {

    private val repo : LocationRepository

    init {
        val database = LocationDatabase.getDatabase(app.applicationContext)
        repo = LocationRepository(database.getLocationDao())
    }

    fun getLocations() : LiveData<List<Location>>? {
        return repo.getAll()
    }
}