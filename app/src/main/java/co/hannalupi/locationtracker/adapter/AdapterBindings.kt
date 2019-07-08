package co.hannalupi.locationtracker.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

class AdapterBindings {

    companion object {

        @JvmStatic
        @BindingAdapter("data")
        fun <T> setRecyclerViewData(recyclerView: RecyclerView, data : T) {
            if(recyclerView.adapter is BindableAdapter<*>) {
                (recyclerView.adapter as BindableAdapter<T>).setData(data)
            }
        }
    }
}