package com.saintrepublic.locationtracker.adapters

import androidx.lifecycle.LifecycleOwner
import com.github.akvast.mvvm.adapter.ViewModelAdapter
import com.saintrepublic.locationtracker.BR
import com.saintrepublic.locationtracker.R
import com.saintrepublic.locationtracker.common.TrackerData

class HistoryAdapter(lifecycleOwner: LifecycleOwner) : ViewModelAdapter(lifecycleOwner) {

    init {
        cell(TrackerData::class.java, R.layout.cell_tracker_results, BR.data)
    }

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return (oldItem as TrackerData).id == (newItem as TrackerData).id
    }

}