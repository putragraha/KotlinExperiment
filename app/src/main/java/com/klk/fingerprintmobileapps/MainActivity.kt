package com.klk.fingerprintmobileapps

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.multidex.MultiDex
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(),
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private val TAG: String? = "MainActivity"

    private val mStaffIdList = arrayOf("001", "002", "003")
    private val mStaffFingerDataList = arrayOf("fd001", "fd002", "fd003")

    private var mStaffId: String? = null
    private var mStaffFingerData: String? = null

    private val customSpinnerAdapter = CustomSpinnerAdapter()

    private var mLocationLong: String? = null
    private var mLocationLat: String? = null

    lateinit var mLocation: Location
    lateinit var mLocationManager: LocationManager
    private var mLocationRequest: LocationRequest? = null
    private lateinit var mGoogleApiClient: GoogleApiClient
    private val listener: LocationListener? = null
    private val UPDATE_INTERVAL = (2 * 1000).toLong()
    private val FASTEST_INTERVAL: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init(){
        setSpinner()
        setButton()
        setLocation()
    }

    private fun setSpinner(){
        customSpinnerAdapter.setAdapter(this, spinnerStaffId, mStaffIdList)
        customSpinnerAdapter.setAdapter(this, spinnerStaffFingerData, mStaffFingerDataList)

        spinnerStaffId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    mStaffId = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        spinnerStaffFingerData.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    mStaffFingerData = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun setButton(){
        btnScan.setOnClickListener {
            var valid: Boolean = checkValid()

            if (valid) {
                getResult()
            } else {
                Toast.makeText(this,
                        "Gagal",
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setLocation(){
        MultiDex.install(this)
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        checkLocation()
    }

    private fun checkLocation(): Boolean{
        if (!isLocationEnabled())
            showAlert()
        return isLocationEnabled()
    }

    private fun isLocationEnabled(): Boolean {
        mLocationManager = getSystemService(Context.LOCATION_SERVICE)  as LocationManager
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showAlert(){
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
                .setPositiveButton("Location Settings", DialogInterface.OnClickListener { paramDialogInterface, paramInt ->
                    val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(myIntent)
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { paramDialogInterface, paramInt -> })
        dialog.show()
    }

    private fun checkValid(): Boolean{
        var valid: Boolean = false
        if ((mStaffId.equals("001") && mStaffFingerData.equals("fd001"))
                || (mStaffId.equals("002") && mStaffFingerData.equals("fd002"))
                || (mStaffId.equals("003") && mStaffFingerData.equals("fd003"))) {
            valid = true
        }

        return valid
    }

    private fun getResult(){
        // get current date
        val date = Date();
        val formatter = SimpleDateFormat("dd MMM yyyy HH:mm")
        val answer: String = formatter.format(date)

        Toast.makeText(this,
                "Staff ID : ${mStaffId} | " +
                        "Date : ${answer} | " +
                        "Longitude : ${mLocationLong} | " +
                        "Latitude : ${mLocationLat}",
                Toast.LENGTH_LONG).show()
    }

    protected fun startLocationUpdates(){
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    override fun onStart() {
        super.onStart()
        if (mGoogleApiClient != null){
            mGoogleApiClient.connect()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect()
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        mGoogleApiClient.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.w(TAG, "Connection Failed. Error: ${connectionResult.errorCode
        }")
    }

    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        startLocationUpdates()

        var fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this, OnSuccessListener<Location> {location ->
            // Got last known location, in some rare situatuin it might be null
            if (location != null){
                mLocation = location
                mLocationLong = mLocation.longitude.toString()
                mLocationLat = mLocation.latitude.toString()
            }
        })
    }

    override fun onLocationChanged(location: Location) {
        mLocation = location
        mLocationLong = mLocation.longitude.toString()
        mLocationLat = mLocation.latitude.toString()
    }

}
