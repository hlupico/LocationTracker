package co.hannalupi.locationtracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import co.hannalupi.locationtracker.R
import co.hannalupi.locationtracker.persistence.Location

class LocationListAdapter(context : Context) : RecyclerView.Adapter<LocationListAdapter.ViewHolder>(), BindableAdapter<LiveData<List<Location>>?> {

    private var data : List<Location> = emptyList()
    private val inflater : LayoutInflater

    init {
        inflater = context.getSystemService(LayoutInflater::class.java)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.location_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = data.get(position)
        holder.latitudeText.text = location.lat.toString()
        holder.longitudeText.text = location.lon .toString()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val latitudeText : TextView = view.findViewById(R.id.latitude)
        val longitudeText : TextView = view.findViewById(R.id.longitude)
    }


    override fun setData(data: LiveData<List<Location>>?) {
        this.data = data?.value ?: emptyList()
        notifyDataSetChanged()
    }
}
