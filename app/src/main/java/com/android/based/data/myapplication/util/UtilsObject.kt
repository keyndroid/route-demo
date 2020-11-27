package com.android.based.data.myapplication.util

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.android.based.data.myapplication.service.RouteService


const val USER_PREF="userPref"
const val USER_DATA="userData"
const val ROUTE_DATA="routeData"
const val TRIP_STATUS="tripStatus"
const val STARTFOREGROUND_ACTION = "UpdateLocationService"
const val STARTFOREGROUND_MESSAGE = "UpdateLocationServiceMessage"
const val STOPFOREGROUND_ACTION = "endLocation"

enum class TripStatus(val status:String){
    NotStarted("notStarted"),
    NavigateToStore("navigateToStore"),
    StoreArrived("storeArrived"),
    NavigateToDestination("navigateToDestination"),
    DestinationArrived("destinationArrived"),

}

fun get3CharString(stLat: String): String? {
    return stLat.substring(0, stLat.length - 1)
}


fun UpdateLocationService(
    context: Context,
    isStart: Boolean
) {
    //start
    val message: String
    message = if (isStart) {
        //start service
        STARTFOREGROUND_ACTION
    } else {
        //stop service
        STOPFOREGROUND_ACTION
    }
    context.startService(getLocationServiceIntent(context, message, null))
}

fun UpdateLocationServiceMessage(
    context: Context,
    addressUpdate: String?
) {
    context.startService(
        getLocationServiceIntent(
            context,
            STARTFOREGROUND_MESSAGE,
            addressUpdate
        )
    )
}

fun getLocationServiceIntent(
    context: Context?,
    action: String?,
    message: String?
): Intent? {
    val startIntent = Intent(context, RouteService::class.java)
    startIntent.action = action
    if (!TextUtils.isEmpty(message)) {
        startIntent.putExtra(STARTFOREGROUND_MESSAGE, message)
    }
    return startIntent
}