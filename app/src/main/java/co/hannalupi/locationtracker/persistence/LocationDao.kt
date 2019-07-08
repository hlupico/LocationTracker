package co.hannalupi.locationtracker.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query

@Dao
interface LocationDao {

    @Insert(onConflict = IGNORE)
    suspend fun insert(location : Location) : Long

    @Query ("Select * from location_table ORDER by time ASC")
    fun getAll() : LiveData<List<Location>>
}