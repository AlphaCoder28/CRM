package com.goldmedal.crm.common.LocationManager

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

/**
 * * Uses Google Play API for obtaining device locations
 * * Created by alejandro.tkachuk
 * * alejandro@calculistik.com
 * * www.calculistik.com Mobile Development
 */

@SuppressLint("MissingPermission")
class Wherebout(context: Context) : EasyPermissions.PermissionCallbacks {
    var mcontext: Context? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
 //   private val mFusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
   // private val locationRequest: LocationRequest
    private var currentLocation: Location? = null
 //   val locationSettingsRequest: LocationSettingsRequest
    private var workable: Workable<GPSPoint?>? = null
    private val RC_LOCATION_PERM = 121
    private val TAG = Wherebout::class.java.simpleName
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
    private lateinit var locationManager: LocationManager

    fun onChange(workable: Workable<GPSPoint?>) {
        this.workable = workable
    }

    fun stop() {
        Log.i(TAG, "stop() Stopping location tracking")
        mFusedLocationClient?.removeLocationUpdates(locationCallback)
    }


    init {
        mcontext = context

        if (EasyPermissions.hasPermissions(mcontext!!, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Have permission, do the thing!
            requestLocationUpdates()
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(
                    mcontext as Activity,
                   "This app needs access to your location to check in",
                    RC_LOCATION_PERM,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        }


        val isGPSOn = checkGpsStatus()

        if(isGPSOn){
            if (currentLocation == null) {
                  if (EasyPermissions.hasPermissions(
                                mcontext,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        )
                ) {
                    getDeviceLocation()
                }

            }

        }else{
            buildAlertMessageNoGps()
        }


    }


    private fun requestLocationUpdates(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mcontext!!)

        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 60000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        //TODO: UI updates.
                    }
                }
            }
        }
        mFusedLocationClient?.requestLocationUpdates(mLocationRequest, locationCallback, null)
    }

    private fun getDeviceLocation() {
        Log.d("Wherebout location", "getDeviceLocation: getting the devices current location")

        try {
//            if (mLocationPermissionsGranted) {
            mFusedLocationClient?.lastLocation
                    ?.addOnCompleteListener {

                        if (it.isSuccessful && it.result != null) {
                            Log.d("Wherebout location", "onComplete: found location!")
                            currentLocation = it.result

                            if (currentLocation?.isFromMockProvider!!) {
                                showMockLocationAlert()
                            }

                            val gpsPoint = GPSPoint(currentLocation?.latitude, currentLocation?.longitude)
                            Log.i(TAG, "Location Callback results: $gpsPoint")
                            if (null != workable) {
                                workable!!.work(gpsPoint)
                                stop()
                            }
                        }else {
                            requestLocationUpdates()
                            Log.d("Wherebout location", "onComplete: current location is null")
                        }
                    }

        } catch (e: SecurityException) {
            Log.e("Wherebout location", "getDeviceLocation: SecurityException: " + e.message)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(mcontext as Activity, perms)) {
            SettingsDialog.Builder(mcontext!!).build().show()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        requestLocationUpdates()
        getDeviceLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    private fun checkGpsStatus(): Boolean {
        locationManager = mcontext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!GpsStatus) {
            buildAlertMessageNoGps()
        }
        return GpsStatus
    }


    private fun showMockLocationAlert() {
        val positiveButtonListener = DialogInterface.OnClickListener { dialog12: DialogInterface?, id12: Int -> mcontext!!.startActivity(
                Intent(
                        Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
                )
        ) }
        val builder = AlertDialog.Builder(mcontext!!)
        val titleText = "Mock Location Enabled"
        val foregroundColorSpan = ForegroundColorSpan(mcontext!!.resources.getColor(R.color.holo_red_dark))
        val ssBuilder = SpannableStringBuilder(titleText)
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setTitle(ssBuilder)
                .setMessage(com.goldmedal.crm.R.string.mock_location_alert)
                .setPositiveButton(com.goldmedal.crm.R.string.str_settings, positiveButtonListener)
                .show()
    }


    private fun buildAlertMessageNoGps() {

        val locationMsg = "<b>LOCATION</b>"
        MaterialAlertDialogBuilder(mcontext!!)


                .setMessage(Html.fromHtml("Please turn on $locationMsg to access features in our app"))
                .setCancelable(false)
                .setPositiveButton(mcontext!!.resources.getString(com.goldmedal.crm.R.string.str_ok)) { dialog, which ->
                    mcontext!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    dialog.dismiss()
                }
                .show()
    }

}


//class Wherebout private constructor() {
//
//    private val mFusedLocationClient: FusedLocationProviderClient
//    private val locationCallback: LocationCallback
//    private val locationRequest: LocationRequest
//    val locationSettingsRequest: LocationSettingsRequest
//    private var workable: Workable<GPSPoint?>? = null
//
//    fun onChange(workable: Workable<GPSPoint?>) {
//        this.workable = workable
//    }
//
////    fun onChange(workable: Workable<GPSPoint>?) {
////        this.workable = workable
////    }
//
//    fun stop() {
//        Log.i(TAG, "stop() Stopping location tracking")
//        mFusedLocationClient.removeLocationUpdates(locationCallback)
//    }
//
//    companion object {
//
//        private val instance = Wherebout()
//        private const val RC_LOCATION_PERM = 121
//        private val TAG = Wherebout::class.java.simpleName
//        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
//        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
//        fun instance(): Wherebout {
//            return instance
//        }
//    }
//
//    init {
//        locationRequest = LocationRequest()
//        locationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
//        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        val builder = LocationSettingsRequest.Builder()
//        builder.addLocationRequest(locationRequest)
//        locationSettingsRequest = builder.build()
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult) // why? this. is. retarded. Android.
//                val currentLocation = locationResult.lastLocation
//                val gpsPoint = GPSPoint(currentLocation.latitude, currentLocation.longitude)
//                Log.i(TAG, "Location Callback results: $gpsPoint")
//                if (null != workable) {
//                    workable!!.work(gpsPoint)
//                }
//            }
//        }
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AppDelegate.applicationContext())
//        mFusedLocationClient.requestLocationUpdates(locationRequest,
//                locationCallback, null)
//
//    }
//}