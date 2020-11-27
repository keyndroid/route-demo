package com.android.based.data.myapplication.viewmodel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.based.data.myapplication.util.TripStatus

class LocationViewModel : ViewModel() {
    val locationUpdate=MutableLiveData<Location>()
    val tripStatus=MutableLiveData<TripStatus>()
    val arrivedToLocation=MutableLiveData<Boolean>(false)
    val distanceToDestination=MutableLiveData<String>()



}