package com.android.based.data.myapplication.ui.activity;

import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.android.based.data.myapplication.util.UtilsObjectKt;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

public class MarkerAnimation {
    private String distance = "";
    LatLng endPosition;
    private void animateCar(GoogleMap mMap, Location mCurrentLocation, Marker marker, LatLng endLocation) {
        if (mMap != null && mCurrentLocation != null) {
            endPosition = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//            if (mCurrentLocation != null && mLastLocation != null) {
//                float diff = mLastLocation.distanceTo(mCurrentLocation);
//            }

            /*if (mLastLocation != null && ((mCurrentLocation.getLatitude() != mLastLocation.getLatitude()) && (mCurrentLocation.getLongitude() != mLastLocation.getLongitude()))) {
                bearing = getBearing(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), endPosition);
            }*/


            {
                //New changes
//                 if (isLocationOnPath){
//                //Old logic same as it is
//                //            Log.e("currentDegree", "" + currentDegree);
//                marker.setPosition(endPosition);
//                marker.setAnchor(0.5f, 0.5f);
//                marker.setRotation(bearing - currentDegree);
////            MarkerAnimation.animateMarkerToGB(marker, endPosition, new LatLngInterpolator.Spherical());
//
//
//                CameraPosition cameraPosition = CameraPosition.builder(mMap.getCameraPosition()).zoom(zoom).bearing(bearing - currentDegree)
//                        .target(endPosition).build();
//                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            }else{
////                double calcBearing =  (Math.toDegrees(Math.atan2(y, x))+360.0)%360.0;
//                marker.setRotation(mCurrentLocation.getBearing());
//                marker.setAnchor(0.5f, 0.5f);
//                animateMarker(zoom,marker,new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()),false);
//
////                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            }

                LatLng latLngForUpdate = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                float results2[] = new float[2];
                Location initialPoint = new Location("");
                initialPoint.setLatitude(marker.getPosition().latitude);
                initialPoint.setLongitude(marker.getPosition().longitude);

                Location finalPoint = new Location("");
                finalPoint.setLatitude(latLngForUpdate.latitude);
                finalPoint.setLongitude(latLngForUpdate.longitude);

                String comaprator = "%.5f";

                String stLat = String.format(comaprator, initialPoint.getLatitude());
                String stNewLat = String.format(comaprator, finalPoint.getLatitude());

                String stLng = String.format(comaprator, initialPoint.getLongitude());
                String stNewLng = String.format(comaprator, finalPoint.getLongitude());

                boolean isDirectionChanged = true;
                if (stLat.equalsIgnoreCase(stNewLat) || stLng.equalsIgnoreCase(stNewLng)) {
                    if (UtilsObjectKt.get3CharString(stLat).equalsIgnoreCase(UtilsObjectKt.get3CharString(stNewLat))
                            && UtilsObjectKt.get3CharString(stLng).equalsIgnoreCase(UtilsObjectKt.get3CharString(stNewLng))) {
                        isDirectionChanged = false;
//                        return;
                    }

                }
                if (isDirectionChanged || !isDirectionChanged) {
                    double startLat = Math.toRadians(initialPoint.getLatitude());
                    double startLong = Math.toRadians(initialPoint.getLongitude());
                    double endLat = Math.toRadians(finalPoint.getLatitude());
                    double endLong = Math.toRadians(finalPoint.getLongitude());
                    double direction = initialPoint.bearingTo(finalPoint);
                    double dLong = endLong - startLong;
                    double dPhi = Math.log(Math.tan(endLat / 2.0 + Math.PI / 4.0) / Math.tan(startLat / 2.0 + Math.PI / 4.0));
                    if (Math.abs(dLong) > Math.PI) {
                        if (dLong > 0.0) {
                            dLong = -(2.0 * Math.PI - dLong);
                        } else {
                            dLong = (2.0 * Math.PI + dLong);
                        }
                    }
                    double bearingCalculated = (Math.toDegrees(Math.atan2(dLong, dPhi)) + 360.0) % 360.0;


                    float myNewBearing = getBearing(new LatLng(initialPoint.getLatitude(), initialPoint.getLongitude()), new LatLng(finalPoint.getLatitude(), finalPoint.getLongitude()));
                    if (!Float.isNaN(myNewBearing)){
                        Log.e("BearingCalculate","POint = "+initialPoint+"BearingOLd= "+mCurrentLocation.getBearing());
//                Log.e("BearingCalculate","POint = "+finalPoint+"BearingNew= "+currentDegree);
                        Log.e("BearingCalculate", "Bearing= " + myNewBearing + "Old = " + stLat + "==" + stNewLat + "," + stLng + "==" + stNewLng);
//                marker.setRotation(mCurrentLocation.getBearing());
//                marker.setRotation(currentDegree);
                        marker.setRotation(myNewBearing);
//                marker.setRotation((float) bearingCalculated);
                        marker.setAnchor(0.5f, 0.5f);
                        float zoom = mMap.getCameraPosition().zoom;
                        animateMarker(mMap,zoom, marker, latLngForUpdate, false, myNewBearing);
                    }
//
                }

            }


            /*if (!isCameraAnimating) {
                isCameraAnimating = true;
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                isCameraAnimating = false;
            }*/
            distance = "" + SphericalUtil.computeDistanceBetween(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                    new LatLng(endLocation.latitude, endLocation.longitude));

            calculateDistance();
        }
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private void calculateDistance() {
        if (!TextUtils.isEmpty(distance)) {
            if (Float.parseFloat(distance) > 100) {
            } else {

            }
        }
    }

    interface onArrivedDestination{

    }
    public void animateMarker(final GoogleMap mMap, final float zoom, final Marker marker, final LatLng toPosition,
                              final boolean hideMarker, final float myNewBearing) {


        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;
        final Interpolator interpolator = new LinearInterpolator();

        //TODO for future Start
        /*if (!markerAnimate){
            marker.setPosition(toPosition);
            CameraPosition cameraPosition = CameraPosition.builder(mMap.getCameraPosition()).zoom(zoom).bearing(*//*bearing - *//*myNewBearing)
                    .target(toPosition).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            return;
        }*/
        //TODO for future End
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    CameraPosition cameraPosition = CameraPosition.builder(mMap.getCameraPosition()).zoom(zoom).bearing(/*bearing - */myNewBearing)
                            .target(toPosition).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
}
