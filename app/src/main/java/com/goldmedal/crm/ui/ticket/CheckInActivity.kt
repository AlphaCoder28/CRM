package com.goldmedal.crm.ui.ticket

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.AcceptRejectTicket
import com.goldmedal.crm.data.model.GetTicketDetailsData
import com.goldmedal.crm.util.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.android.synthetic.main.activity_check_in.*
import kotlinx.android.synthetic.main.sheet_map.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM = "model_item"
private const val TAG = "CheckInActivity"

class CheckInActivity : AppCompatActivity(), OnMapReadyCallback, KodeinAware,
    ApiStageListener<Any>, EasyPermissions.PermissionCallbacks {
    private lateinit var mMap: GoogleMap

    private var modelItem: GetTicketDetailsData? = null

    override val kodein by kodein()

    private val factory: TicketViewModelFactory by instance()

    private lateinit var viewModel: TicketViewModel

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null
    private lateinit var locationManager: LocationManager
    private var locationCallback: LocationCallback? = null


    private var custAddressLat: String? = "0.0"
    private var custAddressLong: String? = "0.0"
    private var isGeofenceLock: Boolean? = false
    var custLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in)

        intent?.let {
            modelItem = it.getParcelableExtra(ARG_PARAM)
            if (modelItem != null) {
                Log.d(TAG, "onCreate: " + modelItem?.CustName)
            }

        }

        custLatLng = getLocationFromAddress(this,modelItem?.CustAddress?:"-")

        if(custLatLng != null){
            custAddressLat = custLatLng?.latitude.toString()
            custAddressLong = custLatLng?.longitude.toString()
        }

        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
//        binding.viewmodelAcceptedTicket = viewModel

        viewModel.apiListener = this


        getLocationPermission()
        bindUI()

        btnCall?.setOnClickListener {
            Log.d("TAG", "bind: only call")
            if (!modelItem?.CustContactNo.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = (Uri.parse("tel:" + modelItem?.CustContactNo))
                startActivity(intent)
            }
        }

        btnCheckin?.setOnClickListener {


            viewModel.getLoggedInUser().observe(this@CheckInActivity, Observer { user ->

                if (user != null) {
                    val isGPSOn = checkGpsStatus()
                    if (!isGPSOn) {
                        return@Observer
                    }
                    if (currentLocation == null) {
                        toast("unable to get current location")

                        if (EasyPermissions.hasPermissions(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                            getDeviceLocation()
                        }
                        geoLocate()

                        return@Observer
                    }


                    if (currentLocation?.isFromMockProvider!!) {
                        showMockLocationAlert()
                        return@Observer
                    }

                    val latitude = currentLocation!!.latitude
                    val longitude = currentLocation!!.longitude


                    if (modelItem?.isGeoFenceLock == true) {
                        if (custAddressLat.isNullOrEmpty() || custAddressLong.isNullOrEmpty()) {
                            toast("Customer Location cannot be found,Please contact Admin")
                            return@Observer
                        }

                        isGeofenceLock = true

                    } else {
                        if (checkIfWithinGeofenceRange()) {
                            isGeofenceLock = true
                         //   alertDialog("You are inside geofence")
                        } else {
//                            alertDialog(getString(R.string.str_check_in_alert))
                            isGeofenceLock = false
                        }
                    }


                    viewModel.checkIn(
                        user.UserId,
                        modelItem?.TicketID ?: -1,
                        latitude.toString(),
                        longitude.toString(),
                        getAddressFromLatLong(this, latitude, longitude) ?: "Unnamed Road",
                            isGeofenceLock?:false
                    )

                }
            })


        }

        imv_back?.setOnClickListener {
            finish()
        }
    }


    fun checkIfWithinGeofenceRange(): Boolean {

        val results1 = FloatArray(1)
        Location.distanceBetween(
            currentLocation!!.latitude,
            currentLocation!!.longitude,
            custAddressLat!!.toDouble(),
            custAddressLong!!.toDouble(),
            results1
        )
        val distanceInMeters = results1[0]
      //  alertDialog(""+distanceInMeters)
        return distanceInMeters < GEOFENCE_RADIUS
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
       // Log.d(TAG, getString(R.string.log_permissions_denied, requestCode, perms.size))


        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms[0])) {
            SettingsDialog.Builder(this).build().show()
        }
    }


    override fun onResume() {
        super.onResume()
        checkGpsStatus()
    }

    override fun onStop() {
        super.onStop()
        locationCallback?.let {
            fusedLocationClient?.removeLocationUpdates(it)
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        requestLocationUpdates()
        getDeviceLocation()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    // ============================================================================================
    //  Private Methods
    // ============================================================================================

    @AfterPermissionGranted(RC_LOCATION_PERM)
    private fun getLocationPermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Have permission, do the thing!
            requestLocationUpdates()
            initMap()

        } else {
            // Request one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_location_rationale_message),
                RC_LOCATION_PERM,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }
    //TODO Added by sajid kantharia 27 Mar 2021
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(){


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
            fusedLocationClient?.requestLocationUpdates(mLocationRequest, locationCallback, null)
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


//    private fun initLocationServices() {
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        mLocationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {}
//        }
//    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            getDeviceLocation()

        }
        geoLocate()


        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun bindUI() {
        txtCustomerName?.text = modelItem?.CustName
        text_view_customer_address?.text = modelItem?.CustAddress

        if (modelItem?.IsSCAddressverified == true) {
            txt_verification?.text = getString(R.string.str_verified_address)
            imv_verification_status?.setImageResource(R.drawable.ic_verified)
        } else {
            txt_verification?.text = getString(R.string.str_un_verified_address)
            imv_verification_status?.setImageResource(R.drawable.ic_unverified)
        }


    }


    private fun checkGpsStatus(): Boolean {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!GpsStatus) {
            buildAlertMessageNoGps()
        }
        return GpsStatus
    }

    private fun showMockLocationAlert() {
        val positiveButtonListener = DialogInterface.OnClickListener { dialog12: DialogInterface?, id12: Int -> startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
            )
        ) }
        val builder = AlertDialog.Builder(this)
        val titleText = "Mock Location Enabled"
        val foregroundColorSpan = ForegroundColorSpan(resources.getColor(android.R.color.holo_red_dark))
        val ssBuilder = SpannableStringBuilder(titleText)
        ssBuilder.setSpan(
            foregroundColorSpan,
            0,
            titleText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setTitle(ssBuilder)
            .setMessage(R.string.mock_location_alert)
            .setPositiveButton(R.string.str_settings, positiveButtonListener)
            .show()
    }

    private fun buildAlertMessageNoGps() {

        val locationMsg = "<b>LOCATION</b>"
        MaterialAlertDialogBuilder(this)


            .setMessage(Html.fromHtml("Please turn on $locationMsg to access features in our app"))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()


    }

    private fun getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location")

        try {
//            if (mLocationPermissionsGranted) {
                fusedLocationClient?.lastLocation
                    ?.addOnCompleteListener {

                        if (it.isSuccessful && it.result != null) {
                            Log.d(TAG, "onComplete: found location!")
                             currentLocation = it.result

                        }else {
                            requestLocationUpdates()
                            Log.d(TAG, "onComplete: current location is null")
                            Toast.makeText(
                                this@CheckInActivity,
                                "unable to get current location",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

        } catch (e: SecurityException) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.message)
        }
    }


    private fun geoLocate() {
        Log.d(TAG, "geoLocate: geolocating")
        val searchString: String = modelItem?.CustAddress ?: ""
        val geocoder = Geocoder(this@CheckInActivity)
        var list: List<Address> = ArrayList()
        try {
            list = geocoder.getFromLocationName(searchString, 1)
        } catch (e: IOException) {
            Log.e(TAG, "geoLocate: IOException: " + e.localizedMessage)
        }
        if (list.size > 0) {
            val address: Address = list[0]
            Log.d(TAG, "geoLocate: found a location: " + address.toString())
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(
                LatLng(address.latitude, address.longitude), DEFAULT_ZOOM,
                address.getAddressLine(0)
            )
        }
    }

    private fun moveCamera(latLng: LatLng, zoom: Float, title: String) {
        Log.d(
            TAG,
            "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        if (title != "My Location") {
            val options = MarkerOptions()
                .position(latLng)
                .title(title)
            mMap.addMarker(options)
        }
        //hideSoftKeyboard()
    }

    companion object {

        private const val DEFAULT_ZOOM = 15f
        private const val RC_LOCATION_PERM = 121
        private const val GEOFENCE_RADIUS = 500.0f // in meters

       // private const val REFRESH_REQUEST_CODE = 101
        fun start(context: Context, item: GetTicketDetailsData?) {
            val intent = Intent(context, CheckInActivity::class.java)
            intent.putExtra(ARG_PARAM, item)
            context.startActivity(intent)

         //   context.startActivityForResult(intent)

        }
    }

    override fun onStarted(callFrom: String) {
        progress_bar?.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        progress_bar?.stop()

        val data = _object as List<AcceptRejectTicket?>
        if (!data.isNullOrEmpty()) {
           toast("Checked in successfully")


            val intent = Intent(this, TicketInfoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
            intent.putExtra(ARG_PARAM, modelItem)
            startActivity(intent)
            finish()

        }
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        progress_bar?.stop()


    }

    override fun onValidationError(message: String, callFrom: String) {
        myCoordinatorLayout?.snackbar(message)
    }
}