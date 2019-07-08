package co.hannalupi.locationtracker.repo

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import co.hannalupi.locationtracker.persistence.Location
import co.hannalupi.locationtracker.persistence.LocationDao

class LocationRepository(val dao : LocationDao) {

    @WorkerThread
    suspend fun insert(location : Location) {
        val id = dao.insert(location)
        Log.d("LocationRepository", "Inserted " + id.toString())
    }

    fun getAll() : LiveData<List<Location>> {
        return dao.getAll()
    }
}