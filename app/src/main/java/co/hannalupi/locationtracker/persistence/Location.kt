package co.hannalupi.locationtracker.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
data class Location(@ColumnInfo(name = "time") val time : Long, val lat : Double, val lon : Double, @PrimaryKey(autoGenerate = true) val id : Int = 0)
