package com.android.based.data.myapplication.pref

import android.content.Context
import android.content.SharedPreferences
import com.android.based.data.myapplication.ui.model.OrderData
import com.android.based.data.myapplication.util.*
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson

class UserPref(val context: Context) {
    val mGson = Gson()
    private fun getPreference(): SharedPreferences {
        return context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)
    }

    private fun getEditor(): SharedPreferences.Editor {
        return getPreference().edit()
    }

    public fun saveTrip(orderData: OrderData?) {
        getEditor()
            .putString(USER_DATA, mGson.toJson(orderData))
            .apply()
    }
    public fun getTrip(): OrderData? {
        val orderData=getPreference().getString(USER_DATA,null)
        if (orderData==null){
            return null
        }else{
            return mGson.fromJson(orderData,OrderData::class.java)
        }
    }

    fun saveCurrentRoute(polylineOptions: PolylineOptions?) {
        getEditor()
            .putString(ROUTE_DATA, mGson.toJson(polylineOptions))
            .apply()
    }
    public fun getCurrentRoute(): PolylineOptions? {
        val currentRoute=getPreference().getString(ROUTE_DATA,null)
        if (currentRoute==null){
            return null
        }else{
            return mGson.fromJson(currentRoute,PolylineOptions::class.java)
        }
    }

    fun saveTripStatus(tripStatus: TripStatus?) {
        if (tripStatus==null){
            getEditor()
                .putString(TRIP_STATUS, null)
                .apply()
            return
        }
        getEditor()
            .putString(TRIP_STATUS, mGson.toJson(tripStatus))
            .apply()
    }
    public fun getCurrentTripStatus(): TripStatus {
        val tripStatus=getPreference().getString(TRIP_STATUS,null)
        if (tripStatus==null){
            return TripStatus.NotStarted
        }else{
            return mGson.fromJson(tripStatus,TripStatus::class.java)
        }
    }

    fun clearTrip(){
        saveTrip(null)
        saveCurrentRoute(null)
        saveTripStatus(null)
    }

}