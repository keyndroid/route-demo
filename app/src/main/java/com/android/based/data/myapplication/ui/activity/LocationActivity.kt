package com.android.based.data.myapplication.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android.based.data.myapplication.viewmodel.LocationViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*


abstract class LocationActivity : AppCompatActivity() {

    abstract fun onLocation(it: Location)
    private val REQUEST_CHECK_SETTINGS = 101;
    protected val viewModel: LocationViewModel by lazy { ViewModelProvider(this).get(LocationViewModel::class.java) }
    private val mFusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            this
        )
    }
    /*fun settingsrequest() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(30 * 1000)
        locationRequest.setFastestInterval(5 * 1000)
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true) //this is the key ingredient
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result.setResultCallback(object : ResultCallback<LocationSettingsResult?> {
            *//*override fun onResult(p0: LocationSettingsResult) {
                TODO("Not yet implemented")
            }*//*
            override fun onResult(result: LocationSettingsResult) {
                val status: Status = result.getStatus()
                val state: LocationSettingsStates = result.getLocationSettingsStates()
                when (status.getStatusCode()) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                         // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                this@LocationActivity,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (e: SendIntentException) {
                            // Ignore the error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        })
    }*/



    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> startLocationUpdates()
                Activity.RESULT_CANCELED -> locationUpdate() //keep asking if imp or do whatever
            }
        }
    }

    private fun startLocationUpdates() {
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it!=null){
                    onLocation(it)
                }else{
                    startLocationUpdates()
                }
            }
            .addOnFailureListener {
                startLocationUpdates()
            }
    }


    /*protected fun createLocationRequest() {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task =
            client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) {
//            Toast.makeText(this@LocationActivity, "addOnSuccessListener", Toast.LENGTH_SHORT).show()
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            startLocationUpdates()
        }
        task.addOnFailureListener(this) { e ->
//            Toast.makeText(this@LocationActivity, "addOnFailureListener", Toast.LENGTH_SHORT).show()
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(
                        this@LocationActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }*/
    protected fun locationUpdate(){
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.setSmallestDisplacement(1f)
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        //                                .addLocationRequest(mLocationRequestBalancedPowerAccuracy);
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
            mLocationRequest,
            mLocationCallback, null
        )
        val task =
            LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
//        updateLocation()
        task.addOnSuccessListener {
            startLocationUpdates()
        }
        task.addOnCompleteListener { task ->
            try {
                val response = task.getResult(
                    ApiException::class.java
                )
               /* Toast.makeText(
                    this@LocationActivity,
                    "Fetching Current Location",
                    Toast.LENGTH_SHORT
                ).show()*/

                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                                             // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable =
                                exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                this@LocationActivity,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (e: SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }


    }
    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult == null) {
                return
            }
            if (locationResult.lastLocation!=null){
                viewModel.locationUpdate.value=locationResult.lastLocation
            }

            /* for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    //TODO: UI updates.
                    checkLocation(location);
                }
            }*/
            updateLocation(locationResult.lastLocation)
        }
    }

    private fun updateLocation(lastLocation: Location) {

    }

}