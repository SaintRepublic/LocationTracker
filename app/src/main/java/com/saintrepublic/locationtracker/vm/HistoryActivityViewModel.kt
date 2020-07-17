package com.saintrepublic.locationtracker.vm

import androidx.lifecycle.MutableLiveData
import com.saintrepublic.locationtracker.common.TrackerData
import com.saintrepublic.locationtracker.database.DatabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryActivityViewModel(val databaseService: DatabaseService) : BaseViewModel() {

    val items = MutableLiveData<List<TrackerData>>(emptyList())

    fun load() {
        launch(Dispatchers.IO) {
            val list = databaseService.selectAll()
            items.postValue(list)
        }
    }

}