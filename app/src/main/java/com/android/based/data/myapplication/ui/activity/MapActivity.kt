package com.android.based.data.myapplication.ui.activity

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import androidx.databinding.DataBindingUtil
import com.android.based.data.android_based_data_capture.libs.EnumHelper
import com.android.based.data.myapplication.R
import com.android.based.data.myapplication.databinding.ActivityMapBinding
import com.android.based.data.myapplication.libs.permission.CallbackPermission
import com.android.based.data.myapplication.libs.permission.PermissionUtils
import com.android.based.data.myapplication.pref.UserPref
import com.android.based.data.myapplication.util.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import java.text.DecimalFormat
import java.util.*


class MapActivity : LocationUpdateActivity(), OnMapReadyCallback {
    val userPref: UserPref by lazy { UserPref(this) }

    private lateinit var mMap: GoogleMap
    private lateinit var markerBounceUtils: MarkerBounceUtils
    val timer = Timer()
    lateinit var marker: Marker
    lateinit var destinationMarker: Marker
    lateinit var binding: ActivityMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        init()
        initListener()
        initObserver()
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        gestureEnabled(false)
        val latLong = mMap.getCameraPosition().target

        val TutorialsPoint = latLong
        val markerOption = getMarkerOption(TutorialsPoint)
        marker = mMap.addMarker(markerOption)
        mMap.setOnMarkerClickListener(OnMarkerClickListener { true })
        mMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint))
        markerBounceUtils = MarkerBounceUtils(marker)
        markerBounceUtils.startAnimation()

        val updateProfile: TimerTask = CustomTimerTask(this, marker)
        timer.scheduleAtFixedRate(updateProfile, 10, 2000)
        // Add a marker in Sydney and move the camera
        // Add a marker in Sydney and move the camera

        mMap.setOnCameraMoveStartedListener {
            if (it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
//                Toast.makeText(this, "The user gestured on the map.", Toast.LENGTH_SHORT).show();
            } else if (it == GoogleMap.OnCameraMoveStartedListener
                    .REASON_API_ANIMATION) {
//                Toast.makeText(this, "The user tapped something on the map.", Toast.LENGTH_SHORT).show();
            } else if (it == GoogleMap.OnCameraMoveStartedListener
                    .REASON_DEVELOPER_ANIMATION) {
//                Toast.makeText(this, "The app moved the camera.", Toast.LENGTH_SHORT).show();
            }
        }
        askPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.trip_menu, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.itemExit -> {
                completeTrip()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLocation(it: Location) {
        val endLocation: LatLng =
            LocationTracker.getLocationFromAddress(this, userPref.getTrip()?.storeLocation)
        addDestinationMarker()


        timer.cancel()
        val TutorialsPoint = LatLng(it.latitude, it.longitude)
        val markerOption = getMarkerOption(TutorialsPoint)
        marker.remove()
        marker = mMap.addMarker(markerOption)
        mMap.setOnMarkerClickListener(OnMarkerClickListener { true })

        val builder = LatLngBounds.Builder()
        builder.include(marker.position)
        if (::destinationMarker.isInitialized){
            builder.include(destinationMarker.position)
        }

        val bounds = builder.build()

//        val padding = 10 // offset from edges of the map in pixels

//        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.10).toInt() // offset from edges of the map 10% of screen


        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        mMap.moveCamera(cu);



        if (userPref.getCurrentRoute() == null) {
            LocationTracker(TutorialsPoint, this, endLocation, object :
                LocationTracker.OnRouteCompletion {
                override fun onFailure() {
//                    Toast.makeText(this@MapActivity,"No Route Found",Toast.LENGTH_LONG).show()
                    processRoute()
                }

                override fun onComplete(polylineOptions: PolylineOptions) {
                    Gson().toJson(polylineOptions)
                    userPref.saveCurrentRoute(polylineOptions)
                    drawRoute()
                    processRoute()
                }
            })
        } else {
            drawRoute()
            processRoute()
        }

    }

    private fun processRoute() {
        viewModel.tripStatus.value = userPref.getCurrentTripStatus()
    }

    private fun drawRoute() {
        mMap.addPolyline(userPref.getCurrentRoute())
    }

    private fun init() {
        val mapFragment: SupportMapFragment? = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun initObserver() {
        viewModel.tripStatus.observe(this, androidx.lifecycle.Observer {
            var isStarted=true
            when (it.status) {
                TripStatus.NotStarted.status -> {
                    binding.btStartTrip.visibility = View.VISIBLE
                    binding.btStartDestination.visibility = View.GONE
                    binding.btCompleteTrip.visibility = View.GONE
                    gestureEnabled(false)
                    isStarted=false
                }
                TripStatus.NavigateToStore.status -> {
                    binding.btStartTrip.visibility = View.GONE
                    binding.btStartDestination.visibility = View.GONE
                    binding.btCompleteTrip.visibility = View.GONE
                    gestureEnabled(true)
                    moveMarker()
                }
                TripStatus.StoreArrived.status -> {
                    binding.btStartTrip.visibility = View.GONE
                    binding.btStartDestination.visibility = View.VISIBLE
                    binding.btCompleteTrip.visibility = View.GONE
                    gestureEnabled(false)

                    removeDestination()

                }
                TripStatus.NavigateToDestination.status -> {
                    binding.btStartTrip.visibility = View.GONE
                    binding.btStartDestination.visibility = View.GONE
                    binding.btCompleteTrip.visibility = View.GONE
                    gestureEnabled(true)

                    addDestinationMarker()
                    moveMarker()
                }
                TripStatus.DestinationArrived.status -> {
                    binding.btStartTrip.visibility = View.GONE
                    binding.btStartDestination.visibility = View.GONE
                    binding.btCompleteTrip.visibility = View.VISIBLE
                    gestureEnabled(false)
                    removeDestination()
                }
            }
            if (isStarted){
                val message=getDestinationLocationMessage()

                UpdateLocationServiceMessage(this,message)
            }

        })

        viewModel.locationUpdate.observe(this, androidx.lifecycle.Observer {
            val tripStatus = viewModel.tripStatus.value
            if (tripStatus != null) {
                if (tripStatus.status.equals(TripStatus.NavigateToStore.status)

                ) {
                    val endLocation: LatLng =
                        LocationTracker.getLocationFromAddress(this, userPref.getTrip()?.storeLocation)
                    animateCar(mMap,it,marker,endLocation)
                }
                else if (tripStatus.status.equals(TripStatus.NavigateToDestination.status)){
                    val endLocation: LatLng =
                        LocationTracker.getLocationFromAddress(this, userPref.getTrip()?.deliveryLocation)
                    animateCar(mMap,it,marker,endLocation)
                }

            }
        })
        viewModel.arrivedToLocation.observe(this, androidx.lifecycle.Observer {
            if (it){
                when(viewModel.tripStatus.value?.status){
                    TripStatus.NavigateToStore.status ->{
                        val updatedStatus=TripStatus.StoreArrived
                        userPref.saveTripStatus(updatedStatus)
                        viewModel.tripStatus.value=updatedStatus


                        val message=getDestinationLocationMessage()

                        UpdateLocationServiceMessage(this,message)
                    }
                    TripStatus.NavigateToDestination.status ->{
                        val updatedStatus=TripStatus.DestinationArrived
                        userPref.saveTripStatus(updatedStatus)
                        viewModel.tripStatus.value=updatedStatus
                        val message=getDestinationLocationMessage()

                        UpdateLocationServiceMessage(this,message)
                    }
                }
            }
        })
        viewModel.distanceToDestination.observe(this, androidx.lifecycle.Observer {
            if (it!=null){
                val df = DecimalFormat()
                df.setMaximumFractionDigits(2)

                val message=StringBuilder()
                val miles: Double = it.toFloat() * 0.00062137119

                message.append(df.format(miles))
                message.append(" ")
                message.append(" mi")
                when(viewModel.tripStatus.value?.status){
                    TripStatus.NavigateToStore.status ->{
                        val messageLocation=StringBuilder(getDestinationLocationMessage())
                        messageLocation.append("\n")
                        messageLocation.append(message.toString())
                        UpdateLocationServiceMessage(this,messageLocation.toString())
                    }
                    TripStatus.NavigateToDestination.status ->{
                        val messageLocation=StringBuilder(getDestinationLocationMessage())
                        messageLocation.append("\n")
                        messageLocation.append(message.toString())
                        UpdateLocationServiceMessage(this,messageLocation.toString())
                    }
                }
            }
        })
    }

    private fun addDestinationMarker() {

        val currentStatus = userPref.getCurrentTripStatus().status

        val endLocation: LatLng
        if (currentStatus.equals(TripStatus.NavigateToStore.status)){
            endLocation=
            LocationTracker.getLocationFromAddress(this, userPref.getTrip()?.storeLocation)
        }else if (currentStatus.equals(TripStatus.NavigateToDestination.status)){
            endLocation = LocationTracker.getLocationFromAddress(this, userPref.getTrip()?.deliveryLocation)
        }
        else{
            return
        }
        val markerDestinationOption = getMarkerDestinationOption(endLocation)
        destinationMarker = mMap.addMarker(markerDestinationOption)
    }

    private fun removeDestination() {

        if (::destinationMarker.isInitialized){
            destinationMarker.remove()
        }
    }

    private fun moveMarker() {

        val location = CameraUpdateFactory.newLatLngZoom(
            marker.position, 17f
        )
        mMap.moveCamera(location)
    }

    private fun showMarker(currentLocation: Location) {
       /* val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        MarkerAnimation.animateMarkerToGB(
            marker,
            latLng,
            LatLngInterpolator.Spherical())*/

    }
    private fun initListener() {
        binding.btStartTrip.setOnClickListener {
            val updatedStatus=TripStatus.NavigateToStore
            userPref.saveTripStatus(updatedStatus)
            viewModel.tripStatus.value = updatedStatus
        }
        binding.btStartDestination.setOnClickListener {
            val updatedStatus=TripStatus.NavigateToDestination
            userPref.saveTripStatus(updatedStatus)
            viewModel.tripStatus.value = updatedStatus
        }
        binding.btCompleteTrip.setOnClickListener { completeTrip() }
    }

    private fun completeTrip() {
        userPref.clearTrip()
        HomeActivity.start(this)
    }

    private fun getMarkerOption(tutorialsPoint: LatLng): MarkerOptions {
        return MarkerOptions()
            .position(tutorialsPoint)
            .flat(true)
            .rotation(/*bearing +*/ 0f)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_top_down_car))
//            .anchor(0.5f, 0.5f)
            .zIndex(1000f)

    }

    private fun getMarkerDestinationOption(tutorialsPoint: LatLng): MarkerOptions {
        return MarkerOptions()
            .position(tutorialsPoint)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination))
            .anchor(0.5f, 0.5f)

    }

    private fun gestureEnabled(isEnable: Boolean) {
        mMap.getUiSettings().setAllGesturesEnabled(isEnable)
        mMap.getUiSettings().isCompassEnabled=false
    }

    private fun askPermission() {

        val permissionBuilder = PermissionUtils.Builder().setContenxt(this)

        permissionBuilder.addPermission(
//            EnumHelper.Permission.BACKGROUND_LOCATION,
            EnumHelper.Permission.LOCATION
        )
        permissionBuilder.setCallbackPermission(object : CallbackPermission {
                override fun onPermissionDenied() {
                    //Log.e("PermissionMyCheck","PermissionMyCheck==Get Lost")
                    finish()
                }

                override fun onPermissionGranted() {
                    locationUpdate()
                }
            })
            .build()
    }

    /**
     * Performs a bounce animation on a [Marker].
     */
    private class BounceAnimation private constructor(
        private val mStart: Long,
        private val mDuration: Long,
        private val mMarker: Marker,
        handler: Handler
    ) :
        Runnable {
        private val mInterpolator: Interpolator
        private val mHandler: Handler
        override fun run() {
            val elapsed = SystemClock.uptimeMillis() - mStart
            val t = Math.max(
                1 - mInterpolator.getInterpolation(elapsed.toFloat() / mDuration),
                0f
            )
            mMarker.setAnchor(0.5f, 1.0f + 1.2f * t)
            if (t > 0.0) {
                // Post again 16ms later.
                mHandler.postDelayed(this, 16L)
            }
        }

        init {
            mHandler = handler
            mInterpolator = BounceInterpolator()
        }
    }

    private fun getDestinationLocationMessage(): String {
        var message=""
        val tripStatus=viewModel.tripStatus.value
        if (tripStatus!=null){
            val storeLocation = userPref.getTrip()?.storeLocation
            val destinationLocation = userPref.getTrip()?.deliveryLocation
            if (tripStatus.status.equals(TripStatus.NavigateToStore.status)){
                message = getString(R.string.msg_towards_store,storeLocation)
            }
            else if (tripStatus.status.equals(TripStatus.StoreArrived.status)){
                message = getString(R.string.msg_arrived_store,storeLocation)
            }
            else if (tripStatus.status.equals(TripStatus.NavigateToDestination.status)){
                message = getString(R.string.msg_towards_destination,destinationLocation)

            }
            else if (tripStatus.status.equals(TripStatus.DestinationArrived.status)){
                message = getString(R.string.msg_arrived_store,destinationLocation)
            }
        }
        return message

    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MapActivity::class.java))
        }
    }
}